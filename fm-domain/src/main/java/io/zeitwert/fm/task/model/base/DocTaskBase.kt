package io.zeitwert.fm.task.model.base

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
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

	private val _relatedObjId = addBaseProperty("relatedObjId", Any::class.java)
	private val _relatedDocId = addBaseProperty("relatedDocId", Any::class.java)
	private val _subject = addBaseProperty("subject", String::class.java)
	private val _content = addBaseProperty("content", String::class.java)
	private val _isPrivate = addBaseProperty("isPrivate", Boolean::class.java)
	private val _priority = addEnumProperty("priority", CodeTaskPriority::class.java)
	private val _dueAt = addBaseProperty("dueAt", OffsetDateTime::class.java)
	private val _remindAt = addBaseProperty("remindAt", OffsetDateTime::class.java)

	override val repository get() = super.repository as DocTaskRepository

	fun accountRepository() = directory.getRepository(ObjAccount::class.java) as ObjAccountRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): DocTask = this

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this._caption.value = subject
	}

	override val account get() = if (accountId != null) accountRepository().get(accountId!!) else null

	override var relatedToId: Any?
		get() = _relatedObjId.value ?: _relatedDocId.value
		set(id) {
			if (id == null) {
				_relatedObjId.value = null
				_relatedDocId.value = null
			} else {
				_relatedObjId.value = id
				_relatedDocId.value = null
			}
		}

	override val relatedTo get() = null

}
