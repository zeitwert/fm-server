
package fm.comunas.ddd.obj.model.base;

import java.time.OffsetDateTime;
import java.util.Collection;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.ddd.aggregate.model.base.AggregateBase;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateType;
import fm.comunas.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjMeta;
import fm.comunas.ddd.obj.model.ObjPartItem;
import fm.comunas.ddd.obj.model.ObjPartTransition;
import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.obj.service.api.ObjService;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.PartListProperty;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;

public abstract class ObjBase extends AggregateBase implements Obj, ObjMeta {

	private final SessionInfo sessionInfo;
	private final ObjRepository<? extends Obj, ? extends Record> repository;
	private final UpdatableRecord<?> objDbRecord;
	private final CodeAggregateTypeEnum aggregateTypeEnum;

	protected final SimpleProperty<Integer> id;
	protected final ReferenceProperty<ObjTenant> tenant;
	protected final ReferenceProperty<ObjUser> owner;
	protected final SimpleProperty<String> caption;
	protected final ReferenceProperty<ObjUser> createdByUser;
	protected final SimpleProperty<OffsetDateTime> createdAt;
	protected final ReferenceProperty<ObjUser> modifiedByUser;
	protected final SimpleProperty<OffsetDateTime> modifiedAt;

	private final SimpleProperty<String> objTypeId;

	private final PartListProperty<ObjPartTransition> transitionList;

	protected ObjBase(SessionInfo sessionInfo, ObjRepository<? extends Obj, ? extends Record> repository,
			UpdatableRecord<?> objDbRecord) {
		this.sessionInfo = sessionInfo;
		this.repository = repository;
		this.objDbRecord = objDbRecord;
		this.aggregateTypeEnum = AppContext.getInstance().getEnumeration(CodeAggregateTypeEnum.class);
		this.id = this.addSimpleProperty(objDbRecord, ObjFields.ID);
		this.tenant = this.addReferenceProperty(objDbRecord, ObjFields.TENANT_ID, ObjTenant.class);
		this.owner = this.addReferenceProperty(objDbRecord, ObjFields.OWNER_ID, ObjUser.class);
		this.caption = this.addSimpleProperty(objDbRecord, ObjFields.CAPTION);
		this.createdByUser = this.addReferenceProperty(objDbRecord, ObjFields.CREATED_BY_USER_ID, ObjUser.class);
		this.createdAt = this.addSimpleProperty(objDbRecord, ObjFields.CREATED_AT);
		this.modifiedByUser = this.addReferenceProperty(objDbRecord, ObjFields.MODIFIED_BY_USER_ID, ObjUser.class);
		this.modifiedAt = this.addSimpleProperty(objDbRecord, ObjFields.MODIFIED_AT);
		this.objTypeId = this.addSimpleProperty(objDbRecord, ObjFields.OBJ_TYPE_ID);
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
	public void doInit(Integer objId, Integer tenantId, Integer userId) {
		this.objTypeId.setValue(this.getRepository().getAggregateType().getId());
		this.id.setValue(objId);
		this.tenant.setId(tenantId);
		this.createdByUser.setId(userId);
		this.createdAt.setValue(OffsetDateTime.now());
	}

	@Override
	public void doStore(Integer userId) {
		UpdatableRecord<?> dbRecord = (UpdatableRecord<?>) getObjDbRecord();
		dbRecord.setValue(ObjFields.MODIFIED_BY_USER_ID, userId);
		dbRecord.setValue(ObjFields.MODIFIED_AT, OffsetDateTime.now());
		dbRecord.store();
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

	public abstract void loadTransitionList(Collection<ObjPartTransition> nodeList);

	// TODO get rid
	private Class<? extends Obj> getInstanceClass() {
		if (this.getAggregateType() == aggregateTypeEnum.getItem("obj_contact")) {
			return ObjContact.class;
		} else if (this.getAggregateType() == aggregateTypeEnum.getItem("obj_account")) {
			return ObjAccount.class;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <O extends Obj> O getInstance() {
		return (O) this.getAppContext().getRepository(this.getInstanceClass()).get(this.getSessionInfo(), this.getId())
				.get();
	}

	protected void doCalcAll() {
	}

}
