package io.dddrive.domain.task.model.base // Correct package

import io.dddrive.ddd.model.PartRepository
import io.dddrive.doc.model.base.DocPartBase
import io.dddrive.property.model.Property
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import java.time.OffsetDateTime

abstract class DocTaskPartCommentBase(
	// Renamed
	task: DocTask,
	repository: PartRepository<DocTask, DocTaskPartComment>, // Renamed
	property: Property<*>,
	id: Int,
) : DocPartBase<DocTask>(task, repository, property, id),
	DocTaskPartComment {

	protected val _text = addBaseProperty("text", String::class.java)
	protected val _createdAt = addBaseProperty("createdAt", OffsetDateTime::class.java)

	override fun doAfterCreate() {
		this._createdAt.value = OffsetDateTime.now()
	}

	override fun delete() {}

}
