package io.zeitwert.fm.account.service.api;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

import java.util.List;

public interface AccountService {

	List<ObjAccountVRecord> getAccountList(SessionInfo sessionInfo, ObjTenant tenant);

}
