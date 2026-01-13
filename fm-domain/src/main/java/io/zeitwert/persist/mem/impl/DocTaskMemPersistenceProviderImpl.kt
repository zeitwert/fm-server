package io.zeitwert.persist.mem.impl

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.persist.DocTaskPersistenceProvider
import io.zeitwert.persist.mem.base.DocMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for DocTask.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("docTaskPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class DocTaskMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : DocMemPersistenceProviderBase<DocTask>(DocTask::class.java),
	DocTaskPersistenceProvider
