package dddrive.domain.task.model.impl

import dddrive.app.doc.model.base.DocRepositoryBase
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.model.DocTaskPartComment
import dddrive.domain.task.model.DocTaskRepository
import dddrive.domain.task.persist.DocTaskPersistenceProvider
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
