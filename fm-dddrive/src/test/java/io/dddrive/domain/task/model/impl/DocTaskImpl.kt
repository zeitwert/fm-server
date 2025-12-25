package io.dddrive.domain.task.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.doc.model.base.DocBase
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import java.time.OffsetDateTime

open class DocTaskImpl(
	override val repository: DocTaskRepository,
	isNew: Boolean,
) : DocBase(repository, isNew),
	DocTask {

	override var subject: String? by baseProperty()
	override var content: String? by baseProperty()
	override var isPrivate: Boolean? by baseProperty()
	override var priority: CodeTaskPriority? by enumProperty()
	override var dueAt: OffsetDateTime? by baseProperty()
	override var remindAt: OffsetDateTime? by baseProperty()

	override val commentList: PartListProperty<DocTaskPartComment> by partListProperty()

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === commentList) {
			return directory
				.getPartRepository(DocTaskPartComment::class.java)
				.create(this, property, partId) as
				Part<*>
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
