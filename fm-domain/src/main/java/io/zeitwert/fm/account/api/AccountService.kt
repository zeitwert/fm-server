package io.zeitwert.fm.account.api

import io.zeitwert.app.api.jsonapi.EnumeratedDto

interface AccountService {

	/**
	 * Get all accounts of a tenant, without any security check.
	 *
	 * @param tenantId tenant id
	 * @return accounts of the tenant
	 */
	fun getAccounts(tenantId: Any): List<EnumeratedDto>

}
