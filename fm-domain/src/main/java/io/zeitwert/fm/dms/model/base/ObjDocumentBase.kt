package io.zeitwert.fm.dms.model.base

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin
import java.time.OffsetDateTime

abstract class ObjDocumentBase(
	repository: ObjDocumentRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjDocument,
	AggregateWithNotesMixin, AggregateWithTasksMixin {

	override fun aggregate(): ObjDocument = this

	private val _name = addBaseProperty("name", String::class.java)
	private val _documentKind = addEnumProperty("documentKind", CodeDocumentKind::class.java)
	private val _documentCategory = addEnumProperty("documentCategory", CodeDocumentCategory::class.java)
	private val _templateDocument = addReferenceProperty("templateDocument", ObjDocument::class.java)
	private val _contentKind = addEnumProperty("contentKind", CodeContentKind::class.java)

	private var _contentType: CodeContentType? = null
	private var _content: ByteArray? = null

	override val repository get() = super.repository as ObjDocumentRepository

	override fun doAfterLoad() {
		super.doAfterLoad()
		this.loadContent()
	}

	override val account
		get() = if (accountId != null) directory.getRepository(ObjAccount::class.java).get(this.accountId!!) else null

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

	// override fun doCalcSearch() {
	//     super.doCalcSearch()
	//     this.addSearchText(this.name)
	// }

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
		this._caption.value = this.name
	}

}
