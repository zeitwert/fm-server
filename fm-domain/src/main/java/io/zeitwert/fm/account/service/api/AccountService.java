package io.zeitwert.fm.account.service.api;

import io.dddrive.oe.model.ObjTenant;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

import java.util.List;

public interface AccountService {

	/**
	 * Get all accounts of a tenant, without any security check.
	 * 
	 * @param tenant tenant
	 * @return accounts of the tenant
	 */
	List<ObjAccountVRecord> getAccounts(ObjTenant tenant);

}
