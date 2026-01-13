package io.zeitwert.persist.mem.impl

import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.persist.ObjNotePersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjNote.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objNotePersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjNoteMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjNote>(ObjNote::class.java),
	ObjNotePersistenceProvider
