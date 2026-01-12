package io.zeitwert.persist.mem

import dddrive.app.obj.model.Obj
import io.zeitwert.persist.ObjPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for base Obj.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<Obj>(Obj::class.java),
	ObjPersistenceProvider {

	override fun isObj(id: Any): Boolean = id is Int
}
