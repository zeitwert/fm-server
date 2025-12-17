package io.dddrive.domain.task.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.doc.model.base.DocBase
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.Property
import io.dddrive.domain.task.model.DocTask
import io.dddrive.domain.task.model.DocTaskPartComment
import io.dddrive.domain.task.model.DocTaskRepository
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

@Suppress("ktlint")
abstract class DocTaskBase(
	repository: DocTaskRepository,
	isNew: Boolean,
) : DocBase(repository), DocTask {

	//@formatter:off
	private val _subject: BaseProperty<String> = this.addBaseProperty("subject", String::class.java)
	private val _content: BaseProperty<String> = this.addBaseProperty("content", String::class.java)
	private val _isPrivate: BaseProperty<Boolean> = this.addBaseProperty("private", Boolean::class.java)
	private val _priority: EnumProperty<CodeTaskPriority> = this.addEnumProperty("priority", CodeTaskPriority::class.java)
	private val _dueAt: BaseProperty<OffsetDateTime> = this.addBaseProperty("dueAt", OffsetDateTime::class.java)
	private val _remindAt: BaseProperty<OffsetDateTime> = this.addBaseProperty("remindAt", OffsetDateTime::class.java)
	private val _commentList: PartListProperty<DocTaskPartComment> = this.addPartListProperty("commentList", DocTaskPartComment::class.java)
	//@formatter:on

	override fun doAddPart(property: Property<*>, partId: Int?): Part<*> {
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
