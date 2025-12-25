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
		DocTaskRepository::class.java,
		DocTask::class.java,
		DocTaskImpl::class.java,
		AGGREGATE_TYPE,
	),
	DocTaskRepository {

	override val persistenceProvider get() = directory.getPersistenceProvider(DocTask::class.java) as DocTaskPersistenceProvider

	override fun registerParts() {
		super.registerParts()
		this.addPart(DocTask::class.java, DocTaskPartComment::class.java, DocTaskPartCommentImpl::class.java)
	}

	companion object {

		const val AGGREGATE_TYPE = "docTask"
	}

}
