package io.zeitwert.persist.mem.impl

import dddrive.app.obj.model.Obj
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.ObjPersistenceProvider
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for base Obj.
 *
 * Active when zeitwert.persistence_type=mem
 */
@Component("objPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "mem")
class ObjMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjMemPersistenceProviderBase<Obj>(Obj::class.java),
	ObjPersistenceProvider {

	override fun isObj(id: Any): Boolean = id is Int
}
