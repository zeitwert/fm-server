
package io.zeitwert.ddd.oe.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.List;

public abstract class ObjUserBase extends ObjBase implements ObjUser {

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;

	private final UpdatableRecord<?> dbRecord;

	public ObjUserBase(SessionInfo sessionInfo, ObjUserRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> userRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = userRecord;
		this.name = this.addSimpleProperty(dbRecord, ObjUserFields.NAME);
		this.description = this.addSimpleProperty(dbRecord, ObjUserFields.DESCRIPTION);
	}

	@Override
	public ObjUserRepository getRepository() {
		return (ObjUserRepository) super.getRepository();
	}

	public String getEmail() {
		return this.dbRecord.getValue(ObjUserFields.EMAIL);
	}

	public String getPassword() {
		return this.dbRecord.getValue(ObjUserFields.PASSWORD);
	}

	public List<CodeUserRole> getRoleList() {
		List<String> roles = List.of(this.dbRecord.getValue(ObjUserFields.ROLE_LIST).split(","));
		return roles.stream().map(r -> CodeUserRoleEnum.getUserRole(r)).toList();
	}

	public boolean hasRole(CodeUserRole role) {
		List<String> roles = List.of(this.dbRecord.getValue(ObjUserFields.ROLE_LIST).split(","));
		return roles.contains(role.getId());
	}

	public String getPicture() {
		return this.dbRecord.getValue(ObjUserFields.PICTURE);
	}

}
