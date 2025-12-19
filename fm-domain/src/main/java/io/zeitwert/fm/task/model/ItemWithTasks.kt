package io.zeitwert.fm.task.model

import java.time.OffsetDateTime

interface ItemWithTasks {

	val tasks: List<DocTask>

	fun addTask(userId: Any, timestamp: OffsetDateTime): DocTask

}
