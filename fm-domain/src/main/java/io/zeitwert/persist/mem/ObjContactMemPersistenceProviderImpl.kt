package io.zeitwert.persist.mem

import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.persist.ObjContactPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjContact.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objContactPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjContactMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjContact>(ObjContact::class.java),
	ObjContactPersistenceProvider
