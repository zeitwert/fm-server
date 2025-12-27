package dddrive.domain.task.model.impl

import dddrive.app.doc.model.base.DocPartBase
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.model.Property
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.model.DocTaskPartComment
import java.time.OffsetDateTime

class DocTaskPartCommentImpl(
	task: DocTask,
	override val repository: PartRepository<DocTask, DocTaskPartComment>,
	property: Property<*>,
	id: Int,
) : DocPartBase<DocTask>(task, repository, property, id),
	DocTaskPartComment {

	override var text: String? by baseProperty(this, "text")
	private var _createdAt: OffsetDateTime? by baseProperty(this, "createdAt")
	override val createdAt: OffsetDateTime? get() = _createdAt

	override fun doAfterCreate() {
		super.doAfterCreate()
		_createdAt = OffsetDateTime.now()
	}

	override fun delete() {}

}
