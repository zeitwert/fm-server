package io.zeitwert.persist.mem

import dddrive.db.MemoryDb
import dddrive.query.query
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.persist.ObjUserPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.*

/**
 * Memory-based persistence provider for ObjUser.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objUserPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjUserMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjUser>(ObjUser::class.java),
	ObjUserPersistenceProvider {

	override fun getByEmail(email: String): Optional<Any> {
		val userMap =
			MemoryDb
				.find(intfClass, query { filter { "email" eq email } })
				.firstOrNull()
		return Optional.ofNullable(userMap?.get("id"))
	}
}
