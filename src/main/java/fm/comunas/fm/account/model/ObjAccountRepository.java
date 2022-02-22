
package fm.comunas.fm.account.model;

import fm.comunas.ddd.obj.model.ObjRepository;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.account.model.db.tables.records.ObjAccountVRecord;

import java.util.Optional;

public interface ObjAccountRepository extends ObjRepository<ObjAccount, ObjAccountVRecord> {

	Optional<ObjAccount> getByKey(SessionInfo sessionInfo, String key);

}
