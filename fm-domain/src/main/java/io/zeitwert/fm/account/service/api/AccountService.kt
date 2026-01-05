package io.zeitwert.fm.account.service.api

import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord
import io.zeitwert.fm.oe.model.ObjTenant

interface AccountService {

	/**
	 * Get all accounts of a tenant, without any security check.
	 *
	 * @param tenant tenant
	 * @return accounts of the tenant
	 */
	fun getAccounts(tenant: ObjTenant): List<ObjAccountVRecord>

}
