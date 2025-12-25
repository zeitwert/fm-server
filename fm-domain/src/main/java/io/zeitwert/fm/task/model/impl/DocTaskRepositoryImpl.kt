package io.zeitwert.fm.task.model.impl

import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.stereotype.Component

@Component("docTaskRepository")
class DocTaskRepositoryImpl :
	FMDocRepositoryBase<DocTask>(
		DocTaskRepository::class.java,
		DocTask::class.java,
		DocTaskImpl::class.java,
		AGGREGATE_TYPE_ID,
	),
	DocTaskRepository {

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc_task"
	}

}
