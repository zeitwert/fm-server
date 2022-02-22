
package fm.comunas.ddd.oe.model.base;

import fm.comunas.ddd.obj.model.base.ObjBase;
import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.oe.model.ObjTenantRepository;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.session.model.SessionInfo;

import org.jooq.UpdatableRecord;

public abstract class ObjTenantBase extends ObjBase implements ObjTenant {

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;

	private final UpdatableRecord<?> dbRecord;

	public ObjTenantBase(SessionInfo sessionInfo, ObjTenantRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> tenantRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = tenantRecord;
		this.name = this.addSimpleProperty(dbRecord, ObjTenantFields.NAME);
		this.description = this.addSimpleProperty(dbRecord, ObjTenantFields.DESCRIPTION);
	}

	@Override
	public ObjTenantRepository getRepository() {
		return (ObjTenantRepository) super.getRepository();
	}

	public String getExtlKey() {
		return this.dbRecord.getValue(ObjTenantFields.EXTL_KEY);
	}

	@Override
	public void doCalcAll() {

	}

}
