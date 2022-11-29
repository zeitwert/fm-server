
package io.zeitwert.fm.account.model;

import java.util.Optional;

import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjAccountRepository extends FMObjRepository<ObjAccount, ObjAccountVRecord> {

	Optional<ObjAccount> getByKey(String key);

}
