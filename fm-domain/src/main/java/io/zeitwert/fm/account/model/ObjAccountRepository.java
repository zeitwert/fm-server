
package io.zeitwert.fm.account.model;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocumentRepository;

public interface ObjAccountRepository extends ObjRepository<ObjAccount> {

	ObjContactRepository getContactRepository();

	// TODO-MIGRATION: DMS - uncomment after DMS is migrated
	// ObjDocumentRepository getDocumentRepository();

}
