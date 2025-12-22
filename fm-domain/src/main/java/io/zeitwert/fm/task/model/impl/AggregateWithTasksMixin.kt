package io.zeitwert.fm.task.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.obj.model.Obj
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
			return this.taskRepository().getByForeignKey(fkName, id).map { it -> taskRepository().get(it) }
		}

	override fun addTask(userId: Any, timestamp: OffsetDateTime): DocTask {
		val task = this.taskRepository().create(this.aggregate().tenantId, userId, timestamp)
		task.relatedToId = this.aggregate().id
		return task
	}

}
