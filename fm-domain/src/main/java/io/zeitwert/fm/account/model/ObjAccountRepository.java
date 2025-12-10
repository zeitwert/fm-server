
package io.zeitwert.fm.account.model;

import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.service.api.ObjContactCache;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjAccountRepository extends FMObjRepository<ObjAccount, ObjAccountVRecord> {

	ObjContactRepository getContactRepository();

	ObjContactCache getContactCache();

	ObjDocumentRepository getDocumentRepository();

}
