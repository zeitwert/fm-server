
package fm.comunas.ddd.oe.model.base;

import org.jooq.UpdatableRecord;

import fm.comunas.ddd.obj.model.base.ObjBase;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.oe.model.ObjUserRepository;
import fm.comunas.ddd.oe.model.enums.CodeUserRole;
import fm.comunas.ddd.oe.model.enums.CodeUserRoleEnum;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.session.model.SessionInfo;

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

	public String getPicture() {
		return this.dbRecord.getValue(ObjUserFields.PICTURE);
	}

	@Override
	public void doCalcAll() {

	}

}
