package io.zeitwert.fm.dms.model.impl

import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.delegate.referenceProperty
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin
import java.time.OffsetDateTime

class ObjDocumentImpl(
	override val repository: ObjDocumentRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjDocument,
	AggregateWithNotesMixin,
	AggregateWithTasksMixin {

	override var name by baseProperty<String>("name")
	override var contentKind by enumProperty<CodeContentKind>("contentKind")
	override var documentKind by enumProperty<CodeDocumentKind>("documentKind")
	override var documentCategory by enumProperty<CodeDocumentCategory>("documentCategory")
	override var templateDocumentId by referenceIdProperty<ObjDocument>("templateDocument")
	override val templateDocument by referenceProperty<ObjDocument>("templateDocument")

	private var _contentType: CodeContentType? = null
	private var _content: ByteArray? = null

	override val contentType get() = _contentType
	override val content get() = _content

	override fun storeContent(
		contentType: CodeContentType,
		content: ByteArray,
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		this.repository.storeContent(this, contentType, content, userId, timestamp)
		this._contentType = contentType
		this._content = content
		this.calcAll()
	}

	// ItemWithAccount implementation
	override val account
		get() = if (accountId != null) directory.getRepository(ObjAccount::class.java).get(accountId!!) else null

	override fun aggregate(): ObjDocument = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = directory.getRepository(DocTask::class.java) as DocTaskRepository

	override fun doAfterLoad() {
		super.doAfterLoad()
		this.loadContent()
	}

	override fun doAfterStore() {
		super.doAfterStore()
		this.loadContent()
	}

	private fun loadContent() {
		this._contentType = this.repository.getContentType(this)
		if (this._contentType != null) {
			this._content = this.repository.getContent(this)
		}
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(name)
	}

}
