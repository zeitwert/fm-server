
package io.zeitwert.ddd.obj.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.time.OffsetDateTime;

import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.base.AggregateBase;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.db.model.jooq.AggregateStateImpl;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjMeta;
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

	private final AggregateState state;
	private final UpdatableRecord<?> baseDbRecord;
	private final UpdatableRecord<?> extnDbRecord;

	//@formatter:off
	protected final SimpleProperty<Integer> id = this.addSimpleProperty("id", Integer.class);
	protected final SimpleProperty<String> objTypeId = this.addSimpleProperty("objTypeId", String.class);
	protected final ReferenceProperty<ObjTenant> tenant = this.addReferenceProperty("tenant", ObjTenant.class);
	protected final ReferenceProperty<ObjUser> owner = this.addReferenceProperty("owner", ObjUser.class);
	protected final SimpleProperty<String> caption = this.addSimpleProperty("caption", String.class);
	protected final SimpleProperty<Integer> version = this.addSimpleProperty("version", Integer.class);
	protected final ReferenceProperty<ObjUser> createdByUser = this.addReferenceProperty("createdByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> createdAt = this.addSimpleProperty("createdAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> modifiedByUser = this.addReferenceProperty("modifiedByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> modifiedAt = this.addSimpleProperty("modifiedAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> closedByUser = this.addReferenceProperty("closedByUser", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> closedAt = this.addSimpleProperty("closedAt", OffsetDateTime.class);
	protected final PartListProperty<ObjPartTransition> transitionList = this.addPartListProperty("transitionList", ObjPartTransition.class);
	//@formatter:on

	protected ObjBase(ObjRepository<? extends Obj, ? extends TableRecord<?>> repository,
			UpdatableRecord<?> baseDbRecord,
			UpdatableRecord<?> extnDbRecord) {
		this.repository = repository;
		this.state = null;
		this.baseDbRecord = baseDbRecord;
		this.extnDbRecord = extnDbRecord;
	}

	protected ObjBase(ObjRepository<? extends Obj, ? extends TableRecord<?>> repository, AggregateState state) {
		this.repository = repository;
		this.state = state;
		this.baseDbRecord = null;
		this.extnDbRecord = null;
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

	@Override
	public AggregateState getAggregateState() {
		return this.state;
	}

	public final UpdatableRecord<?> baseDbRecord() {
		if (this.state != null) {
			return ((AggregateStateImpl) this.state).getBaseRecord();
		} else {
			return this.baseDbRecord;
		}
	}

	public final UpdatableRecord<?> extnDbRecord() {
		if (this.state != null) {
			return ((AggregateStateImpl) this.state).getExtnRecord();
		} else {
			return this.extnDbRecord;
		}
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
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.transitionList)) {
			return this.getRepository().getTransitionRepository().create(this, partListType);
		}
		assertThis(false, "could instantiate part for partListType " + partListType);
		return null;
	}

	protected void setCaption(String caption) {
		this.caption.setValue(caption);
	}

}
