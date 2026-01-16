package io.zeitwert.persist.mem.impl

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.persist.ObjContactPersistenceProvider
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjContact.
 *
 * Active when zeitwert.persistence_type=mem
 */
@Component("objContactPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "mem")
class ObjContactMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjMemPersistenceProviderBase<ObjContact>(ObjContact::class.java),
	ObjContactPersistenceProvider
