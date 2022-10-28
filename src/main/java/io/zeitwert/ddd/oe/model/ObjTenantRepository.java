
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.Optional;

public interface ObjTenantRepository extends ObjRepository<ObjTenant, ObjTenantVRecord> {

	static final int KERNEL_TENANT_ID = 1;

	/**
	 * Lookup Tenant by extl key
	 */
	Optional<ObjTenant> getByExtlKey(SessionInfo sessionInfo, String extlKey);

}
