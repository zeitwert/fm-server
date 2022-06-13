
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
import io.zeitwert.ddd.obj.service.api.ObjService;
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

	protected final SimpleProperty<Integer> id;
	protected final ReferenceProperty<ObjTenant> tenant;
	protected final ReferenceProperty<ObjUser> owner;
	protected final SimpleProperty<String> caption;
	protected final ReferenceProperty<ObjUser> createdByUser;
	protected final SimpleProperty<OffsetDateTime> createdAt;
	protected final ReferenceProperty<ObjUser> modifiedByUser;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;
	protected final ReferenceProperty<ObjUser> closedByUser;
	protected final SimpleProperty<OffsetDateTime> closedAt;

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
	public ObjRepository<? extends Obj, ? extends Record> getRepository() {
		return this.repository;
	}

	protected final UpdatableRecord<?> getObjDbRecord() {
		return this.objDbRecord;
	}

	protected ObjService getObjService() {
		return this.getAppContext().getBean(ObjService.class);
	}

	public CodeAggregateType getAggregateType() {
		return CodeAggregateTypeEnum.getAggregateType(this.objTypeId.getValue());
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.objTypeId.setValue(this.getRepository().getAggregateType().getId());
		this.id.setValue(objId);
		this.tenant.setId(tenantId);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		Integer sessionUserId = this.getMeta().getSessionInfo().getUser().getId();
		this.owner.setId(sessionUserId);
		this.createdByUser.setId(sessionUserId);
		this.createdAt.setValue(OffsetDateTime.now());
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartTransitionRepository transitionRepo = this.getRepository().getTransitionRepository();
		this.transitionList.loadPartList(transitionRepo.getPartList(this, this.getRepository().getTransitionListType()));
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		UpdatableRecord<?> dbRecord = (UpdatableRecord<?>) getObjDbRecord();
		dbRecord.setValue(ObjFields.MODIFIED_BY_USER_ID, this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setValue(ObjFields.MODIFIED_AT, OffsetDateTime.now());
	}

	@Override
	public void doStore() {
		super.doStore();
		getObjDbRecord().store();
	}

	@Override
	public void delete() {
		UpdatableRecord<?> dbRecord = (UpdatableRecord<?>) getObjDbRecord();
		dbRecord.setValue(ObjFields.CLOSED_BY_USER_ID, this.getMeta().getSessionInfo().getUser().getId());
		dbRecord.setValue(ObjFields.CLOSED_AT, OffsetDateTime.now());
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

}
