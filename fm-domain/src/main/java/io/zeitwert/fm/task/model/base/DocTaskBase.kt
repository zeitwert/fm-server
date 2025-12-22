package io.zeitwert.fm.task.model.base

import io.dddrive.property.model.BaseProperty
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
	override val repository: DocTaskRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTask,
	AggregateWithNotesMixin {

	private lateinit var _relatedObjId: BaseProperty<Any>
	private lateinit var _relatedDocId: BaseProperty<Any>

	fun accountRepository() = directory.getRepository(ObjAccount::class.java) as ObjAccountRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): DocTask = this

	override fun doInit() {
		super.doInit()
		_relatedObjId = addBaseProperty("relatedObjId", Any::class.java)
		_relatedDocId = addBaseProperty("relatedDocId", Any::class.java)
		addBaseProperty("subject", String::class.java)
		addBaseProperty("content", String::class.java)
		addBaseProperty("isPrivate", Boolean::class.java)
		addEnumProperty("priority", CodeTaskPriority::class.java)
		addBaseProperty("dueAt", OffsetDateTime::class.java)
		addBaseProperty("remindAt", OffsetDateTime::class.java)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(subject ?: "Aufgabe")
	}

	override val account get() = if (accountId != null) accountRepository().get(accountId!!) else null

	override var relatedToId: Any?
		get() = _relatedObjId.value ?: _relatedDocId.value
		set(id) {
			if (id == null) {
				_relatedObjId.value = null
				_relatedDocId.value = null
			} else { // TODO determine type of id
				_relatedObjId.value = id
				_relatedDocId.value = null
			}
		}

	override val relatedTo get() = null

}
