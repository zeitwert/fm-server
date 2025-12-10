package io.zeitwert.fm.oe.model;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjTenantFMRepository extends ObjRepository<ObjTenantFM> {

	static final int KERNEL_TENANT_ID = 1;

	ObjUserFMRepository getUserRepository();

	ObjDocumentRepository getDocumentRepository();

}
