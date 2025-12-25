package io.dddrive.domain.task.model.impl

import io.dddrive.ddd.model.PartRepository
import io.dddrive.doc.model.base.DocPartBase
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

class DocTaskPartCommentImpl(
	task: DocTask,
	override val repository: PartRepository<DocTask, DocTaskPartComment>,
	property: Property<*>,
	id: Int,
) : DocPartBase<DocTask>(task, repository, property, id),
	DocTaskPartComment {

	override var text: String? by baseProperty()

	// Private mutable backing for read-only interface property
	private var _createdAt: OffsetDateTime? by baseProperty()
	override val createdAt: OffsetDateTime? get() = _createdAt

	// Register createdAt for setValueByPath access (interface has val)
	@Suppress("UNUSED_EXPRESSION")
	override fun doInit() {
		super.doInit()
		_createdAt
	}

	override fun doAfterCreate() {
		super.doAfterCreate()
		_createdAt = OffsetDateTime.now()
	}

	override fun delete() {}

}
