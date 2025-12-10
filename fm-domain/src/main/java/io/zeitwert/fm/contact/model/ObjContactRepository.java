
package io.zeitwert.fm.contact.model;

import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjContactRepository extends FMObjRepository<ObjContact, ObjContactVRecord> {

	ObjAccountCache getAccountCache();

	ObjContactPartAddressRepository getAddressRepository();

}
