
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;

import org.jooq.TableRecord;

public interface ObjTenantRepository extends ObjRepository<ObjTenant, TableRecord<?>> {

	static final int KERNEL_TENANT_ID = 1;

}
