package dddrive.domain.task.model.impl

import dddrive.app.doc.model.base.DocBase
import dddrive.ddd.model.Part
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partListProperty
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

	override var subject by baseProperty<String>("subject")
	override var content by baseProperty<String>("content")
	override var isPrivate by baseProperty<Boolean>("isPrivate")
	override var priority by enumProperty<CodeTaskPriority>("priority")
	override var dueAt by baseProperty<OffsetDateTime>("dueAt")
	override var remindAt by baseProperty<OffsetDateTime>("remindAt")

	override val commentList = partListProperty<DocTask, DocTaskPartComment>("commentList")

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
