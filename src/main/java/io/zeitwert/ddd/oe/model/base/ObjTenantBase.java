
package io.zeitwert.ddd.oe.model.base;

import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.ddd.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.List;

import org.jooq.UpdatableRecord;

public abstract class ObjTenantBase extends ObjBase implements ObjTenant {

	protected final EnumProperty<CodeTenantType> tenantType;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> extlKey;
	protected final SimpleProperty<String> description;

	private final UpdatableRecord<?> dbRecord;

	public ObjTenantBase(SessionInfo sessionInfo, ObjTenantRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> tenantRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = tenantRecord;
		this.tenantType = this.addEnumProperty(dbRecord, ObjTenantFields.TENANT_TYPE_ID, CodeTenantTypeEnum.class);
		this.name = this.addSimpleProperty(dbRecord, ObjTenantFields.NAME);
		this.extlKey = this.addSimpleProperty(dbRecord, ObjTenantFields.EXTL_KEY);
		this.description = this.addSimpleProperty(dbRecord, ObjTenantFields.DESCRIPTION);
	}

	@Override
	public ObjTenantRepository getRepository() {
		return (ObjTenantRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, objId); // tenant.tenantId = tenant.id
		this.dbRecord.setValue(ObjTenantFields.OBJ_ID, objId);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public List<ObjUser> getUsers() {
		SessionInfo sessionInfo = this.getMeta().getSessionInfo();
		ObjUserRepository userRepo = (ObjUserRepository) this.getAppContext().getRepository(ObjUser.class);
		return userRepo.getByForeignKey(sessionInfo, "tenantId", this.getId()).stream()
				.map(c -> userRepo.get(sessionInfo, c.getId())).toList();
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption(this.getName());
	}

}
