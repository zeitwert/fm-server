package io.zeitwert.fm.task.model.base

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

abstract class DocTaskBase(
	repository: DocTaskRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTask,
	AggregateWithNotesMixin {

	// @formatter:off
	private val _relatedObjId: BaseProperty<Int> = this.addBaseProperty("relatedObjId", Int::class.java)
	private val _relatedDocId: BaseProperty<Int> = this.addBaseProperty("relatedDocId", Int::class.java)
	private val _subject: BaseProperty<String> = this.addBaseProperty("subject", String::class.java)
	private val _content: BaseProperty<String> = this.addBaseProperty("content", String::class.java)
	private val _isPrivate: BaseProperty<Boolean> = this.addBaseProperty("isPrivate", Boolean::class.java)
	private val _priority: EnumProperty<CodeTaskPriority> = this.addEnumProperty("priority", CodeTaskPriority::class.java)
	private val _dueAt: BaseProperty<OffsetDateTime> = this.addBaseProperty("dueAt", OffsetDateTime::class.java)
	private val _remindAt: BaseProperty<OffsetDateTime> = this.addBaseProperty("remindAt", OffsetDateTime::class.java)
	// @formatter:on

	override fun getRepository(): DocTaskRepository = super.getRepository() as DocTaskRepository

	override fun noteRepository(): ObjNoteRepository = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): DocTask = this

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this.caption.value = getSubject()
	}

	override fun getAccount(): ObjAccount? = directory.getRepository(ObjAccount::class.java).get(accountId)

	override fun getRelatedToId(): Int? {
		val objId = _relatedObjId.value
		return objId ?: _relatedDocId.value
	}

	override fun setRelatedToId(id: Int?) {
		if (id == null) {
			_relatedObjId.value = null
			_relatedDocId.value = null
		} else {
			_relatedObjId.value = id
			_relatedDocId.value = null
		}
	}

	override fun getRelatedTo(): Aggregate? = null

	override fun getSubject(): String? = _subject.value

	override fun setSubject(subject: String?) {
		_subject.value = subject
	}

	override fun getContent(): String? = _content.value

	override fun setContent(content: String?) {
		_content.value = content
	}

	override fun getIsPrivate(): Boolean? = _isPrivate.value

	override fun setIsPrivate(isPrivate: Boolean?) {
		_isPrivate.value = isPrivate
	}

	override fun getPriority(): CodeTaskPriority? = _priority.value

	override fun setPriority(priority: CodeTaskPriority?) {
		_priority.value = priority
	}

	fun getPriorityId(): String? = _priority.value?.id

	override fun getDueAt(): OffsetDateTime? = _dueAt.value

	override fun setDueAt(dueAt: OffsetDateTime?) {
		_dueAt.value = dueAt
	}

	override fun getRemindAt(): OffsetDateTime? = _remindAt.value

	override fun setRemindAt(remindAt: OffsetDateTime?) {
		_remindAt.value = remindAt
	}

}
