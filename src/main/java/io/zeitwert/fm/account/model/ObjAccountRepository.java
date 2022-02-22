
package io.zeitwert.fm.account.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

import java.util.Optional;

public interface ObjAccountRepository extends ObjRepository<ObjAccount, ObjAccountVRecord> {

	Optional<ObjAccount> getByKey(SessionInfo sessionInfo, String key);

}
