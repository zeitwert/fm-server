package fm.comunas.fm.account.service.api;

import fm.comunas.ddd.oe.model.ObjTenant;
import fm.comunas.ddd.session.model.SessionInfo;
import fm.comunas.fm.account.model.db.tables.records.ObjAccountVRecord;

import java.util.List;

public interface AccountService {

	List<ObjAccountVRecord> getAccountList(SessionInfo sessionInfo, ObjTenant tenant);

}
