package dddrive.domain.task.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.doc.model.base.DocRepositoryBase
import dddrive.domain.ddd.model.impl.SessionContextImpl
import dddrive.domain.doc.persist.base.MapDocPersistenceProviderBase
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.model.DocTaskPartComment
import dddrive.domain.task.model.DocTaskRepository
import org.springframework.stereotype.Component

@Component("docTaskRepository")
class DocTaskRepositoryImpl :
	DocRepositoryBase<DocTask>(
		DocTask::class.java,
		AGGREGATE_TYPE,
	),
	DocTaskRepository {

	override val persistenceProvider = object : MapDocPersistenceProviderBase<DocTask>(DocTask::class.java) {}

	override lateinit var sessionContext: SessionContext

	fun initSessionContext(
		tenantId: Any,
		accountId: Any,
		userId: Any,
	) {
		sessionContext =
			SessionContextImpl(
				tenantId = tenantId,
				accountId = accountId,
				userId = userId,
			)
	}

	override fun createAggregate(isNew: Boolean): DocTask = DocTaskImpl(this, isNew)

	override fun registerParts() {
		super.registerParts()
		this.addPart(DocTaskPartComment::class.java, ::DocTaskPartCommentImpl)
	}

	companion object {

		const val AGGREGATE_TYPE = "docTask"
	}
}
