
package io.zeitwert.fm.oe.model;

import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

public interface ObjTenantFMRepository
		extends ObjRepository<ObjTenantFM, ObjTenantVRecord> {

	static final int KERNEL_TENANT_ID = 1;

}
