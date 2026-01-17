package io.zeitwert.fm.account.api.impl

import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.fm.account.api.AccountService
import io.zeitwert.fm.account.model.db.Tables
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service("accountService")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "sql", matchIfMissing = true)
class AccountSqlServiceImpl(
	val dslContext: DSLContext,
) : AccountService {

	override fun getAccounts(tenantId: Any): List<EnumeratedDto> =
		dslContext
			.selectFrom(Tables.OBJ_ACCOUNT_V)
			.where(Tables.OBJ_ACCOUNT_V.TENANT_ID.eq(tenantId as Int))
			.fetch()
			.map { EnumeratedDto.of(it.id.toString(), it.name) }

}
