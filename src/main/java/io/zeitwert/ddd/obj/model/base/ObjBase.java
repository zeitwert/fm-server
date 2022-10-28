
package io.zeitwert.ddd.obj.model.base;

import java.time.OffsetDateTime;

import org.jooq.Record;
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
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class ObjBase extends AggregateBase implements Obj, ObjMeta {

	private final SessionInfo sessionInfo;
	private final ObjRepository<? extends Obj, ? extends Record> repository;
	private final UpdatableRecord<?> objDbRecord;

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

	protected ObjBase(SessionInfo sessionInfo, ObjRepository<? extends Obj, ? extends Record> repository,
			UpdatableRecord<?> objDbRecord) {
		this.sessionInfo = sessionInfo;
		this.repository = repository;
		this.objDbRecord = objDbRecord;
		this.id = this.addSimpleProperty(objDbRecord, ObjFields.ID);
		this.tenant = this.addReferenceProperty(objDbRecord, ObjFields.TENANT_ID, ObjTenant.class);
		this.owner = this.addReferenceProperty(objDbRecord, ObjFields.OWNER_ID, ObjUser.class);
		this.caption = this.addSimpleProperty(objDbRecord, ObjFields.CAPTION);
		this.version = this.addSimpleProperty(objDbRecord, ObjFields.VERSION);
		this.createdByUser = this.addReferenceProperty(objDbRecord, ObjFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(objDbRecord, ObjFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(objDbRecord, ObjFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(objDbRecord, ObjFields.MODIFIED_AT);
		this.objTypeId = this.addSimpleProperty(objDbRecord, ObjFields.OBJ_TYPE_ID);
		this.closedByUser = this.addReferenceProperty(objDbRecord, ObjFields.CLOSED_BY_USER_ID, ObjUser.class);
		this.closedAt = this.addSimpleProperty(objDbRecord, ObjFields.CLOSED_AT);
		this.transitionList = this.addPartListProperty(repository.getTransitionListType());
	}

	@Override
	public ObjMeta getMeta() {
		return this;
	}

	@Override
	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}

	@Override
	public Integer getTenantId() {
		return this.tenant.getId();
	}

	@Override
	public ObjRepository<? extends Obj, ? extends Record> getRepository() {
		return this.repository;
	}

	protected final UpdatableRecord<?> getObjDbRecord() {
		return this.objDbRecord;
	}

	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.objTypeId.getValue());
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		try {
			this.disableCalc();
			this.objTypeId.setValue(this.getRepository().getAggregateType().getId());
			this.id.setValue(objId);
			this.tenant.setId(tenantId);
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		Integer sessionUserId = this.getMeta().getSessionInfo().getUser().getId();
		try {
			this.disableCalc();
			this.owner.setId(sessionUserId);
			this.version.setValue(0);
			this.createdByUser.setId(sessionUserId);
			this.createdAt.setValue(this.getMeta().getSessionInfo().getCurrentTime());
			this.transitionList.addPart();
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartTransitionRepository transitionRepo = this.getRepository().getTransitionRepository();
		this.transitionList.loadPartList(transitionRepo.getPartList(this, this.getRepository().getTransitionListType()));
	}

	@Override
	public void doBeforeStore() {
		this.transitionList.addPart();
		super.doBeforeStore();
		try {
			this.disableCalc();
			this.version.setValue(this.version.getValue() + 1);
			this.modifiedByUser.setValue(this.getMeta().getSessionInfo().getUser());
			this.modifiedAt.setValue(this.getMeta().getSessionInfo().getCurrentTime());
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doStore() {
		super.doStore();
		getObjDbRecord().store();
	}

	@Override
	public void delete() {
		this.closedByUser.setValue(this.getMeta().getSessionInfo().getUser());
		this.closedAt.setValue(this.getMeta().getSessionInfo().getCurrentTime());
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
