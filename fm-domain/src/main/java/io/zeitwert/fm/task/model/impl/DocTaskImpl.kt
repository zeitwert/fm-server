package io.zeitwert.fm.task.model.impl

import dddrive.app.ddd.model.Aggregate
import dddrive.app.doc.model.Doc
import dddrive.app.obj.model.Obj
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import io.zeitwert.app.doc.model.FMDocRepository
import io.zeitwert.app.doc.model.base.FMDocBase
import io.zeitwert.app.obj.model.FMObjRepository
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

class DocTaskImpl(
	override val repository: DocTaskRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew),
	DocTask,
	AggregateWithNotesMixin {

	override var relatedToId by baseProperty<Any>("relatedToId")
	override var subject by baseProperty<String>("subject")
	override var content by baseProperty<String>("content")
	override var isPrivate by baseProperty<Boolean>("isPrivate")
	override var dueAt by baseProperty<OffsetDateTime>("dueAt")
	override var remindAt by baseProperty<OffsetDateTime>("remindAt")
	override var priority by enumProperty<CodeTaskPriority>("priority")

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

	override val relatedTo: Aggregate?
		get() {
			if (relatedToId == null) return null
			val objRepository = directory.getRepository(Obj::class.java) as FMObjRepository
			if (objRepository.isObj(relatedToId!!)) {
				return objRepository.get(relatedToId!!)
			}
			val docRepository = directory.getRepository(Doc::class.java) as FMDocRepository
			require(docRepository.isDoc(relatedToId!!)) { "relatedToId must refer to Obj or Doc" }
			return docRepository.get(relatedToId!!)
		}

}
