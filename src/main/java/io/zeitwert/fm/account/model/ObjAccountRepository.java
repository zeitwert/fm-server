
package io.zeitwert.fm.account.model;

import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.obj.model.FMObjRepository;

import java.util.Optional;

public interface ObjAccountRepository extends FMObjRepository<ObjAccount, ObjAccountVRecord> {

	Optional<ObjAccount> getByKey(RequestContext requestCtx, String key);

}
