package io.zeitwert.persist.mem

import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.persist.DocTaskPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for DocTask.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("docTaskPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class DocTaskMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<DocTask>(DocTask::class.java),
	DocTaskPersistenceProvider
