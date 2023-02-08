
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;

public interface ObjTenantRepository extends ObjRepository<ObjTenant, Object> {

	static final int KERNEL_TENANT_ID = 1;

}
