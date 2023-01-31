
package io.zeitwert.ddd.obj.model.base;

import java.time.OffsetDateTime;

import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjMeta;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.RequestContext;

public abstract class ObjBase extends AggregateBase implements Obj, ObjMeta {

	private final ObjRepository<? extends Obj, ? extends TableRecord<?>> repository;

	private final UpdatableRecord<?> baseDbRecord;
	private final UpdatableRecord<?> extnDbRecord;

	private final SimpleProperty<Integer> id;
	private final ReferenceProperty<ObjTenant> tenant;
	private final ReferenceProperty<ObjUser> owner;
	private final SimpleProperty<String> caption;
	private final SimpleProperty<Integer> version;
	private final ReferenceProperty<ObjUser> createdByUser;
	private final SimpleProperty<OffsetDateTime> createdAt;
	private final ReferenceProperty<ObjUser> modifiedByUser;
	private final SimpleProperty<OffsetDateTime> modifiedAt;
	private final ReferenceProperty<ObjUser> closedByUser;
	private final SimpleProperty<OffsetDateTime> closedAt;

	private final SimpleProperty<String> objTypeId;

	private final PartListProperty<ObjPartTransition> transitionList;

	protected ObjBase(ObjRepository<? extends Obj, ? extends TableRecord<?>> repository,
			UpdatableRecord<?> baseDbRecord,
			UpdatableRecord<?> extnDbRecord) {
		this.repository = repository;
		this.baseDbRecord = baseDbRecord;
		this.extnDbRecord = extnDbRecord;
		this.id = this.addSimpleProperty(baseDbRecord, ObjFields.ID);
		this.tenant = this.addReferenceProperty(baseDbRecord, ObjFields.TENANT_ID, ObjTenant.class);
		this.owner = this.addReferenceProperty(baseDbRecord, ObjFields.OWNER_ID, ObjUser.class);
		this.caption = this.addSimpleProperty(baseDbRecord, ObjFields.CAPTION);
		this.version = this.addSimpleProperty(baseDbRecord, ObjFields.VERSION);
		this.createdByUser = this.addReferenceProperty(baseDbRecord, ObjFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(baseDbRecord, ObjFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(baseDbRecord, ObjFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(baseDbRecord, ObjFields.MODIFIED_AT);
		this.objTypeId = this.addSimpleProperty(baseDbRecord, ObjFields.OBJ_TYPE_ID);
		this.closedByUser = this.addReferenceProperty(baseDbRecord, ObjFields.CLOSED_BY_USER_ID, ObjUser.class);
		this.closedAt = this.addSimpleProperty(baseDbRecord, ObjFields.CLOSED_AT);
		this.transitionList = this.addPartListProperty(repository.getTransitionListType());
	}

	@Override
	public ObjMeta getMeta() {
		return this;
	}

	@Override
	public RequestContext getRequestContext() {
		return this.getAppContext().getRequestContext();
	}

	@Override
	public Integer getTenantId() {
		return this.tenant.getId();
	}

	@Override
	public ObjRepository<? extends Obj, ? extends TableRecord<?>> getRepository() {
		return this.repository;
	}

	protected final UpdatableRecord<?> baseDbRecord() {
		return this.baseDbRecord;
	}

	protected final UpdatableRecord<?> extnDbRecord() {
		return this.extnDbRecord;
	}

	@Override
	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.objTypeId.getValue());
	}

	@Override
	public final void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		try {
			this.disableCalc();
			this.objTypeId.setValue(this.getRepository().getAggregateType().getId());
			this.id.setValue(objId);
			this.tenant.setId(tenantId);
			if (this.extnDbRecord() != null) {
				this.extnDbRecord().setValue(ObjExtnFields.OBJ_ID, objId);
				// obj_tenant does not have a tenant_id field
				if (this.extnDbRecord().field(ObjExtnFields.TENANT_ID) != null) {
					this.extnDbRecord().setValue(ObjExtnFields.TENANT_ID, tenantId);
				}
			}
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		Integer sessionUserId = this.getMeta().getRequestContext().getUser().getId();
		try {
			this.disableCalc();
			this.owner.setId(sessionUserId);
			this.version.setValue(0);
			this.createdByUser.setId(sessionUserId);
			OffsetDateTime now = this.getMeta().getRequestContext().getCurrentTime();
			this.createdAt.setValue(now);
			ObjPartTransitionBase transition = (ObjPartTransitionBase) this.transitionList.addPart();
			transition.setSeqNr(0);
			transition.timestamp.setValue(now);
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartTransitionRepository transitionRepo = this.getRepository().getTransitionRepository();
		this.transitionList.loadParts(transitionRepo.getParts(this, this.getRepository().getTransitionListType()));
	}

	@Override
	public void doBeforeStore() {

		RequestContext requestCtx = this.getMeta().getRequestContext();
		OffsetDateTime now = requestCtx.getCurrentTime();
		ObjPartTransitionBase transition = (ObjPartTransitionBase) this.transitionList.addPart();
		transition.setSeqNr(this.transitionList.getPartCount() - 1);
		transition.timestamp.setValue(now);

		super.doBeforeStore();

		try {
			this.disableCalc();
			this.version.setValue(this.version.getValue() + 1);
			this.modifiedByUser.setValue(requestCtx.getUser());
			this.modifiedAt.setValue(requestCtx.getCurrentTime());
		} finally {
			this.enableCalc();
		}

	}

	@Override
	public final void doStore() {
		super.doStore();
		this.baseDbRecord().store();
		if (this.extnDbRecord() != null) {
			this.extnDbRecord().store();
		}
	}

	@Override
	public void delete() {
		RequestContext requestCtx = this.getMeta().getRequestContext();
		this.closedByUser.setValue(requestCtx.getUser());
		this.closedAt.setValue(requestCtx.getCurrentTime());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.transitionList) {
			return (P) this.getRepository().getTransitionRepository().create(this, partListType);
		}
		return null;
	}

	@Override
	public ObjPartItem addItem(Property<?> property, CodePartListType partListType) {
		return this.getRepository().getItemRepository().create(this, partListType);
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

}
