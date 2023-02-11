
package io.zeitwert.fm.oe.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

public interface ObjTenantFMRepository
		extends ObjRepository<ObjTenantFM, ObjTenantVRecord> {

	static final int KERNEL_TENANT_ID = 1;

}
