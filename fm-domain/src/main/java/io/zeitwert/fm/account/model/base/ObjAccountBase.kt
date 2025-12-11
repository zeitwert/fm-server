package io.zeitwert.fm.account.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import java.math.BigDecimal

abstract class ObjAccountBase(
    repository: ObjAccountRepository
) : FMObjCoreBase(repository), ObjAccount, AggregateWithNotesMixin {

    override fun aggregate(): ObjAccount = this

    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
    private val _accountType: EnumProperty<CodeAccountType> = this.addEnumProperty("accountType", CodeAccountType::class.java)
    private val _clientSegment: EnumProperty<CodeClientSegment> = this.addEnumProperty("clientSegment", CodeClientSegment::class.java)
    private val _referenceCurrency: EnumProperty<CodeCurrency> = this.addEnumProperty("referenceCurrency", CodeCurrency::class.java)
    private val _inflationRate: BaseProperty<BigDecimal> = this.addBaseProperty("inflationRate", BigDecimal::class.java)
    private val _discountRate: BaseProperty<BigDecimal> = this.addBaseProperty("discountRate", BigDecimal::class.java)
    private val _logoImage: ReferenceProperty<ObjDocument> = this.addReferenceProperty("logoImage", ObjDocument::class.java)
    private val _mainContact: ReferenceProperty<ObjContact> = this.addReferenceProperty("mainContact", ObjContact::class.java)

    override fun getRepository(): ObjAccountRepository {
        return super.getRepository() as ObjAccountRepository
    }

    override fun doAfterCreate(userId: Any?, timestamp: java.time.OffsetDateTime?) {
        super.doAfterCreate(userId, timestamp)
        check(this.id != null) { "id must not be null after create" }
        this.accountId = this.id as Int
        this.addLogoImage(userId, timestamp)
    }

    override fun doBeforeStore(userId: Any?, timestamp: java.time.OffsetDateTime?) {
        super.doBeforeStore(userId, timestamp)
        if (this.getLogoImageId() == null) {
            this.addLogoImage(userId, timestamp)
        }
    }

    // override fun doCalcSearch() {
    //     super.doCalcSearch()
    //     this.addSearchText(this.name)
    //     this.addSearchText(this.description)
    // }

    override fun doCalcAll() {
        super.doCalcAll()
        this.calcCaption()
    }

    private fun calcCaption() {
        this.caption.value = this.name
    }

    private fun addLogoImage(userId: Any?, timestamp: java.time.OffsetDateTime?) {
        val documentRepo = this.getRepository().documentRepository
        val image = documentRepo.create(this.tenantId, userId, timestamp)
        image.name = "Logo"
        image.contentKind = CodeContentKind.getContentKind("foto")
        image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
        image.documentCategory = CodeDocumentCategory.getDocumentCategory("logo")
        documentRepo.store(image, userId, timestamp)
        _logoImage.id = image.id
    }

    override fun getName(): String? = _name.value

    override fun setName(name: String?) {
        _name.value = name
    }

    override fun getDescription(): String? = _description.value

    override fun setDescription(description: String?) {
        _description.value = description
    }

    override fun getAccountType(): CodeAccountType? = _accountType.value

    override fun setAccountType(accountType: CodeAccountType?) {
        _accountType.value = accountType
    }

    override fun getClientSegment(): CodeClientSegment? = _clientSegment.value

    override fun setClientSegment(clientSegment: CodeClientSegment?) {
        _clientSegment.value = clientSegment
    }

    override fun getReferenceCurrency(): CodeCurrency? = _referenceCurrency.value

    override fun setReferenceCurrency(currency: CodeCurrency?) {
        _referenceCurrency.value = currency
    }

    override fun getInflationRate(): BigDecimal? = _inflationRate.value

    override fun setInflationRate(rate: BigDecimal?) {
        _inflationRate.value = rate
    }

    override fun getDiscountRate(): BigDecimal? = _discountRate.value

    override fun setDiscountRate(rate: BigDecimal?) {
        _discountRate.value = rate
    }

    override fun getLogoImageId(): Int? = _logoImage.id as? Int

    override fun getLogoImage(): ObjDocument? = _logoImage.value

    override fun getMainContactId(): Int? = _mainContact.id as? Int

    override fun setMainContactId(id: Int?) {
        _mainContact.id = id
    }

    override fun getMainContact(): ObjContact? = _mainContact.value

    override fun getContacts(): List<ObjContact> {
        val contactRepo = this.getRepository().contactRepository
        return contactRepo.getByForeignKey("accountId", this.id as Any)
    }
}
