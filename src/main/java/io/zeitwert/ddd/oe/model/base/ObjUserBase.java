
package io.zeitwert.ddd.oe.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class ObjUserBase extends ObjBase implements ObjUser {

	protected final SimpleProperty<String> email;
	protected final SimpleProperty<String> password;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<String> picture;
	protected final SimpleProperty<String> role;

	private final UpdatableRecord<?> dbRecord;

	public ObjUserBase(SessionInfo sessionInfo, ObjUserRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> userRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = userRecord;
		this.email = this.addSimpleProperty(dbRecord, ObjUserFields.EMAIL);
		this.password = this.addSimpleProperty(dbRecord, ObjUserFields.PASSWORD);
		this.name = this.addSimpleProperty(dbRecord, ObjUserFields.NAME);
		this.description = this.addSimpleProperty(dbRecord, ObjUserFields.DESCRIPTION);
		this.picture = this.addSimpleProperty(dbRecord, ObjUserFields.PICTURE);
		this.role = this.addSimpleProperty(dbRecord, ObjUserFields.ROLE_LIST);
	}

	@Override
	public ObjUserRepository getRepository() {
		return (ObjUserRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjTenantFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjTenantFields.TENANT_ID, tenantId);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
	}

	public CodeUserRole getRole() {
		return CodeUserRoleEnum.getUserRole(this.dbRecord.getValue(ObjUserFields.ROLE_LIST));
	}

	public boolean hasRole(CodeUserRole role) {
		return this.getRole() == role;
	}

	public void setRole(CodeUserRole role) {
		this.dbRecord.setValue(ObjUserFields.ROLE_LIST, role == null ? null : role.getId());
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
	protected void doCalcAll() {
		super.doCalcAll();
	}

}
