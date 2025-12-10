package io.zeitwert.fm.oe.model;

import io.dddrive.core.obj.model.ObjRepository;
// TODO-MIGRATION: Account - uncomment after Account is migrated
// import io.zeitwert.fm.account.model.ObjAccountRepository;
// import io.zeitwert.fm.account.service.api.ObjAccountCache;
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjTenantFMRepository extends ObjRepository<ObjTenantFM> {

	static final int KERNEL_TENANT_ID = 1;

	ObjUserFMRepository getUserRepository();

	// TODO-MIGRATION: Account - uncomment after Account is migrated
	// ObjAccountRepository getAccountRepository();

	// TODO-MIGRATION: Account - uncomment after Account is migrated
	// ObjAccountCache getAccountCache();

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// ObjDocumentRepository getDocumentRepository();

}
