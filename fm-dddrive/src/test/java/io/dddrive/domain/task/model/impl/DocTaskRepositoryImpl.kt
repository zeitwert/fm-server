package io.dddrive.domain.task.model.impl

import io.dddrive.doc.model.base.DocRepositoryBase
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.persist.DocTaskPersistenceProvider
import org.springframework.stereotype.Component

@Component("docTaskRepository")
class DocTaskRepositoryImpl :
	DocRepositoryBase<DocTask>(
		DocTask::class.java,
		AGGREGATE_TYPE,
	),
	DocTaskRepository {

	override fun createAggregate(isNew: Boolean): DocTask = DocTaskImpl(this, isNew)

	override val persistenceProvider get() = directory.getPersistenceProvider(DocTask::class.java) as DocTaskPersistenceProvider

	override fun registerParts() {
		super.registerParts()
		this.addPart(DocTaskPartComment::class.java, ::DocTaskPartCommentImpl)
	}

	companion object {

		const val AGGREGATE_TYPE = "docTask"
	}

}
