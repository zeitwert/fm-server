package io.zeitwert.persist.mem.impl

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.persist.ObjNotePersistenceProvider
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjNote.
 *
 * Active when zeitwert.persistence_type=mem
 */
@Component("objNotePersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "mem")
class ObjNoteMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjMemPersistenceProviderBase<ObjNote>(ObjNote::class.java),
	ObjNotePersistenceProvider {

	override val hasAccount = false

}
