package io.dddrive.domain.task.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.doc.model.base.DocBase
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

abstract class DocTaskBase(
	override val repository: DocTaskRepository,
	isNew: Boolean,
) : DocBase(repository, isNew),
	DocTask {

	private lateinit var _commentList: PartListProperty<DocTaskPartComment>

	override fun doInit() {
		super.doInit()
		this.addBaseProperty("subject", String::class.java)
		this.addBaseProperty("content", String::class.java)
		this.addBaseProperty("isPrivate", Boolean::class.java)
		this.addEnumProperty("priority", CodeTaskPriority::class.java)
		this.addBaseProperty("dueAt", OffsetDateTime::class.java)
		this.addBaseProperty("remindAt", OffsetDateTime::class.java)
		_commentList = this.addPartListProperty("commentList", DocTaskPartComment::class.java)
	}

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
