
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;

import java.util.Optional;

import org.jooq.TableRecord;

public interface ObjTenantRepository extends ObjRepository<ObjTenant, TableRecord<?>> {

	static final int KERNEL_TENANT_ID = 1;

	/**
	 * Lookup Tenant by extl key
	 */
	Optional<ObjTenant> getByExtlKey(String extlKey);

}
