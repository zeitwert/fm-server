package dddrive.domain.task.persist.impl

import dddrive.domain.doc.persist.base.MapDocPersistenceProviderBase
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.persist.DocTaskPersistenceProvider
import org.springframework.stereotype.Component

/**
 * Map-based persistence provider for DocTask.
 *
 * Automatically serializes/deserializes the aggregate using the property system.
 * No manual PTO mapping required.
 *
 * Active when persistence.type=map
 */
@Component("docTaskPersistenceProvider")
class MapDocTaskPersistenceProviderImpl :
	MapDocPersistenceProviderBase<DocTask>(DocTask::class.java),
	DocTaskPersistenceProvider
