package io.zeitwert.fm.task.model.impl

import io.dddrive.ddd.model.Aggregate
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
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

open class DocTaskImpl(
	override val repository: DocTaskRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTask,
	AggregateWithNotesMixin {

	private var relatedObjId: Any? by baseProperty(this, "relatedObjId")
	private var relatedDocId: Any? by baseProperty(this, "relatedDocId")

	override var subject: String? by baseProperty(this, "subject")
	override var content: String? by baseProperty(this, "content")
	override var isPrivate: Boolean? by baseProperty(this, "isPrivate")
	override var dueAt: OffsetDateTime? by baseProperty(this, "dueAt")
	override var remindAt: OffsetDateTime? by baseProperty(this, "remindAt")
	override var priority: CodeTaskPriority? by enumProperty(this, "priority")

	fun accountRepository() = directory.getRepository(ObjAccount::class.java) as ObjAccountRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun aggregate(): DocTask = this

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(subject ?: "Aufgabe")
	}

	override val account get() = if (accountId != null) accountRepository().get(accountId!!) else null

	override var relatedToId: Any?
		get() = relatedObjId ?: relatedDocId
		set(id) {
			if (id == null) {
				relatedObjId = null
				relatedDocId = null
			} else { // TODO determine type of id
				relatedObjId = id
				relatedDocId = null
			}
		}

	override val relatedTo: Aggregate? get() = null

	// Helper methods for persistence provider to access internal fields
	fun setRelatedIds(
		objId: Int?,
		docId: Int?,
	) {
		relatedObjId = objId
		relatedDocId = docId
	}

	fun getRelatedIds(): Pair<Int?, Int?> = Pair(relatedObjId as Int?, relatedDocId as Int?)

}
