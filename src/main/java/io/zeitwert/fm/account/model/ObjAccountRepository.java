
package io.zeitwert.fm.account.model;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

import java.util.Optional;

public interface ObjAccountRepository extends FMObjRepository<ObjAccount, ObjAccountVRecord> {

	Optional<ObjAccount> getByKey(SessionInfo sessionInfo, String key);

}
