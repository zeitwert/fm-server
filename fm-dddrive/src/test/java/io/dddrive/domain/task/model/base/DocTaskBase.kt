package io.dddrive.domain.task.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.doc.model.base.DocBase
import io.dddrive.core.property.model.Property
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

abstract class DocTaskBase(
	repository: DocTaskRepository,
	isNew: Boolean,
) : DocBase(repository, isNew),
	DocTask {

	protected val _subject = this.addBaseProperty("subject", String::class.java)
	protected val _content = this.addBaseProperty("content", String::class.java)
	protected val _isPrivate = this.addBaseProperty("private", Boolean::class.java)
	protected val _priority = this.addEnumProperty("priority", CodeTaskPriority::class.java)
	protected val _dueAt = this.addBaseProperty("dueAt", OffsetDateTime::class.java)
	protected val _remindAt = this.addBaseProperty("remindAt", OffsetDateTime::class.java)
	protected val _commentList = this.addPartListProperty("commentList", DocTaskPartComment::class.java)

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this._commentList) {
			return this.directory.getPartRepository(DocTaskPartComment::class.java).create(this, property, partId) as Part<*>
		}
		return super.doAddPart(property, partId)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		// Set a default caption if subject is available
		if (subject != null) {
			this.setCaption(subject!!)
		} else {
			this.setCaption("Task: ${this.id}")
		}
	}

}
