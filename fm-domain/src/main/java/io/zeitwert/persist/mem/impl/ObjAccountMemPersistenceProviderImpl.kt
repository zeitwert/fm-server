package io.zeitwert.persist.mem.impl

import dddrive.db.MemoryDb
import dddrive.query.query
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.persist.ObjAccountPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.*

/**
 * Memory-based persistence provider for ObjAccount.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objAccountPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjAccountMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjAccount>(ObjAccount::class.java),
	ObjAccountPersistenceProvider {

	override fun getByKey(key: String): Optional<Any> {
		val accountMap =
			MemoryDb
				.find(intfClass, query { filter { "key" eq key } })
				.firstOrNull()
		return Optional.ofNullable(accountMap?.get("id"))
	}
}
