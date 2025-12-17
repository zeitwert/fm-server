package io.dddrive.domain.task.model.base // Correct package

import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.doc.model.base.DocPartBase
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.Property
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

	// @formatter:off
	private val _text: BaseProperty<String> = addBaseProperty("text", String::class.java)
	private val _createdAt: BaseProperty<OffsetDateTime> = addBaseProperty("createdAt", OffsetDateTime::class.java)
	// @formatter:on

	override fun doAfterCreate() {
		this._createdAt.value = OffsetDateTime.now()
	}

	override fun delete() {}

}
