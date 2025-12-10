package io.zeitwert.fm.dms.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeContentType
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import io.zeitwert.fm.task.model.DocTask
import org.slf4j.LoggerFactory

abstract class ObjDocumentBase(
    repository: ObjDocumentRepository
) : FMObjCoreBase(repository), ObjDocument, AggregateWithNotesMixin {

    override fun aggregate(): ObjDocument = this

    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _documentKind: EnumProperty<CodeDocumentKind> = this.addEnumProperty("documentKind", CodeDocumentKind::class.java)
    private val _documentCategory: EnumProperty<CodeDocumentCategory> = this.addEnumProperty("documentCategory", CodeDocumentCategory::class.java)
    private val _templateDocument: ReferenceProperty<ObjDocument> = this.addReferenceProperty("templateDocument", ObjDocument::class.java)
    private val _contentKind: EnumProperty<CodeContentKind> = this.addEnumProperty("contentKind", CodeContentKind::class.java)

    private var _contentType: CodeContentType? = null
    private var _content: ByteArray? = null

    override fun getRepository(): ObjDocumentRepository {
        return super.getRepository() as ObjDocumentRepository
    }

    override fun doAfterLoad() {
        super.doAfterLoad()
        this.loadContent()
    }

    override fun getAccount(): ObjAccount? {
        return this.getRepository().getAccountRepository()?.get(this.accountId)
    }

    override fun getContentType(): CodeContentType? = _contentType

    override fun getContent(): ByteArray? = _content

    override fun storeContent(contentType: CodeContentType?, content: ByteArray?) {
        this.getRepository().storeContent(this, contentType, content)
        this._contentType = contentType
        this._content = content
        this.calcAll()
    }

    override fun doCalcSearch() {
        super.doCalcSearch()
        this.addSearchText(this.name)
    }

    override fun doAfterStore() {
        super.doAfterStore()
        this.loadContent()
    }

    private fun loadContent() {
        this._contentType = this.getRepository().getContentType(this)
        if (this._contentType != null) {
            this._content = this.getRepository().getContent(this)
        }
    }

    override fun doCalcAll() {
        super.doCalcAll()
        this.calcCaption()
    }

    private fun calcCaption() {
        this.caption.value = this.name
    }

    override fun getName(): String? = _name.value

    override fun setName(name: String?) {
        _name.value = name
    }

    override fun getContentKind(): CodeContentKind? = _contentKind.value

    override fun setContentKind(contentKind: CodeContentKind?) {
        _contentKind.value = contentKind
    }

    override fun getDocumentKind(): CodeDocumentKind? = _documentKind.value

    override fun setDocumentKind(documentKind: CodeDocumentKind?) {
        _documentKind.value = documentKind
    }

    override fun getDocumentCategory(): CodeDocumentCategory? = _documentCategory.value

    override fun setDocumentCategory(documentCategory: CodeDocumentCategory?) {
        _documentCategory.value = documentCategory
    }

    override fun getTemplateDocumentId(): Int? = _templateDocument.id as? Int

    override fun setTemplateDocumentId(id: Int?) {
        _templateDocument.id = id
    }

    override fun getTemplateDocument(): ObjDocument? = _templateDocument.value

    override fun getTasks(): List<DocTask> = emptyList()

    override fun addTask(): DocTask? = null

    companion object {
        private val logger = LoggerFactory.getLogger(ObjDocumentBase::class.java)
    }
}

