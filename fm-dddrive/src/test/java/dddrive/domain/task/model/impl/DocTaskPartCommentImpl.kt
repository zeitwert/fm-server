package dddrive.domain.task.model.impl

import dddrive.app.doc.model.base.DocPartBase
import dddrive.ddd.model.PartRepository
import dddrive.property.delegate.baseProperty
import dddrive.property.model.Property
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

	override var text by baseProperty<String>("text")
	private var _createdAt by baseProperty<OffsetDateTime>("createdAt")
	override val createdAt get() = _createdAt

	override fun doAfterCreate() {
		super.doAfterCreate()
		_createdAt = OffsetDateTime.now()
	}

	override fun delete() {}

}
