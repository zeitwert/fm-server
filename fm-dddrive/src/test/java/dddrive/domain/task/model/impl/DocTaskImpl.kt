package dddrive.domain.task.model.impl

import dddrive.app.doc.model.base.DocBase
import dddrive.ddd.core.model.Part
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import dddrive.domain.task.model.DocTask
import dddrive.domain.task.model.DocTaskPartComment
import dddrive.domain.task.model.DocTaskRepository
import dddrive.domain.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

class DocTaskImpl(
	override val repository: DocTaskRepository,
	isNew: Boolean,
) : DocBase(repository, isNew),
	DocTask {

	override var subject: String? by baseProperty(this, "subject")
	override var content: String? by baseProperty(this, "content")
	override var isPrivate: Boolean? by baseProperty(this, "isPrivate")
	override var priority: CodeTaskPriority? by enumProperty(this, "priority")
	override var dueAt: OffsetDateTime? by baseProperty(this, "dueAt")
	override var remindAt: OffsetDateTime? by baseProperty(this, "remindAt")

	override val commentList: PartListProperty<DocTaskPartComment> = partListProperty(this, "commentList")

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === commentList) {
			return directory.getPartRepository(DocTaskPartComment::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		// Set a default caption if subject is available
		if (subject != null) {
			setCaption(subject!!)
		} else {
			setCaption("Task: $id")
		}
	}

}
