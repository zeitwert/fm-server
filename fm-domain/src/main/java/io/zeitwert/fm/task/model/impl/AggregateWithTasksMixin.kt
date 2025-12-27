package io.zeitwert.fm.task.model.impl

import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.ddd.core.model.Aggregate
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.ItemWithTasks
import java.time.OffsetDateTime

interface AggregateWithTasksMixin : ItemWithTasks {

	fun aggregate(): Aggregate

	fun taskRepository(): DocTaskRepository

	override val tasks: List<DocTask>
		get() {
			val id = this.aggregate().id
			val fkName = if (this is Obj) "relatedObjId" else "relatedDocId"
			return this.taskRepository().getByForeignKey(fkName, id).map { taskRepository().get(it) }
		}

	override fun addTask(
		userId: Any,
		timestamp: OffsetDateTime,
	): DocTask {
		val aggregate = aggregate()
		val tenantId = if (aggregate is Obj) aggregate.tenantId else (aggregate as Doc).tenantId
		val task = this.taskRepository().create(tenantId, userId, timestamp)
		task.relatedToId = aggregate.id
		return task
	}

}
