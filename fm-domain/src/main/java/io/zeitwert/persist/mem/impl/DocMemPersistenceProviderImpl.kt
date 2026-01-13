package io.zeitwert.persist.mem.impl

import dddrive.app.doc.model.Doc
import io.zeitwert.persist.DocPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for base Doc.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("docPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class DocMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<Doc>(Doc::class.java),
	DocPersistenceProvider {

	override fun isDoc(id: Any): Boolean = id is Int
}
