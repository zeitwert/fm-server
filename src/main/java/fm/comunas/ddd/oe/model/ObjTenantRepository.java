
package fm.comunas.ddd.oe.model;

import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.oe.model.db.tables.records.ObjTenantVRecord;

import java.util.Optional;

public interface ObjTenantRepository extends ObjRepository<ObjTenant, ObjTenantVRecord> {

	/**
	 * Lookup tenant with given id (in global session)
	 */
	Optional<ObjTenant> get(Integer id);

	/**
	 * Lookup Tenant by extl key
	 */
	Optional<ObjTenant> getByExtlKey(String extlKey);

}
