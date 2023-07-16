
package io.zeitwert.fm.oe.model;

import io.dddrive.obj.model.ObjRepository;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

public interface ObjTenantFMRepository
		extends ObjRepository<ObjTenantFM, ObjTenantVRecord> {

	static final int KERNEL_TENANT_ID = 1;

	ObjUserFMRepository getUserRepository();

	ObjUserCache getUserCache();

	ObjAccountRepository getAccountRepository();

	ObjAccountCache getAccountCache();

	ObjDocumentRepository getDocumentRepository();

}
