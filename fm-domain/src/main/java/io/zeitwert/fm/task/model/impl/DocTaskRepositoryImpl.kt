package io.zeitwert.fm.task.model.impl

import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.stereotype.Component

@Component("docTaskRepository")
class DocTaskRepositoryImpl :
	FMDocRepositoryBase<DocTask>(
		DocTask::class.java,
		AGGREGATE_TYPE_ID,
	),
	DocTaskRepository {

	override fun createAggregate(isNew: Boolean): DocTask = DocTaskImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc_task"
	}

}
