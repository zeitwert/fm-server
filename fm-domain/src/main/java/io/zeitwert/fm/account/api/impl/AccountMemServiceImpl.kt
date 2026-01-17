package io.zeitwert.fm.account.api.impl

import dddrive.db.MemoryDb
import dddrive.query.query
import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.fm.account.api.AccountService
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service("accountService")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "mem")
class AccountMemServiceImpl(
	val accountRepository: ObjAccountRepository,
) : AccountService {

	override fun getAccounts(tenantId: Any): List<EnumeratedDto> =
		MemoryDb
			.find(ObjAccount::class.java, query { filter { "tenantId" eq tenantId } })
			.map { EnumeratedDto.of(it["id"].toString(), it["name"].toString()) }

}
