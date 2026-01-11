package io.zeitwert.fm.account.api.impl

import io.zeitwert.fm.account.model.db.Tables
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord
import io.zeitwert.fm.account.api.AccountService
import io.zeitwert.fm.oe.model.ObjTenant
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service("accountService")
class AccountServiceImpl(
	private val dslContext: DSLContext,
) : AccountService {

	override fun getAccounts(tenant: ObjTenant): List<ObjAccountVRecord> =
		this.dslContext
			.selectFrom(Tables.OBJ_ACCOUNT_V)
			.where(Tables.OBJ_ACCOUNT_V.TENANT_ID.eq(tenant.id as Int))
			.fetch()

}
