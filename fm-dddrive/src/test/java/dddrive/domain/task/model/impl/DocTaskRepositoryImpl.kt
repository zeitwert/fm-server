package dddrive.domain.task.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.doc.model.base.DocRepositoryBase
import dddrive.domain.doc.persist.base.MapDocPersistenceProviderBase
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.model.DocTaskPartComment
import dddrive.domain.task.model.DocTaskRepository
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component("docTaskRepository")
class DocTaskRepositoryImpl(
	private val sessionContextProvider: ObjectProvider<SessionContext>,
) : DocRepositoryBase<DocTask>(
		DocTask::class.java,
		AGGREGATE_TYPE,
	),
	DocTaskRepository {

	override val sessionContext: SessionContext get() = sessionContextProvider.getObject()

	override val persistenceProvider = object : MapDocPersistenceProviderBase<DocTask>(DocTask::class.java) {}

	override fun createAggregate(isNew: Boolean): DocTask = DocTaskImpl(this, isNew)

	override fun registerParts() {
		super.registerParts()
		this.addPart(DocTaskPartComment::class.java, ::DocTaskPartCommentImpl)
	}

	companion object {

		const val AGGREGATE_TYPE = "docTask"
	}
}
