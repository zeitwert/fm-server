package io.zeitwert.fm.account.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.contact.model.ObjContact
// TODO-MIGRATION: DMS - uncomment after DMS is migrated
// import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import java.math.BigDecimal

/**
 * Base class for ObjAccount using the NEW dddrive framework.
 */
abstract class ObjAccountBase(
    repository: ObjAccountRepository
) : FMObjCoreBase(repository), ObjAccount {

    //@formatter:off
    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
    private val _accountType: EnumProperty<CodeAccountType> = this.addEnumProperty("accountType", CodeAccountType::class.java)
    private val _clientSegment: EnumProperty<CodeClientSegment> = this.addEnumProperty("clientSegment", CodeClientSegment::class.java)
    private val _referenceCurrency: EnumProperty<CodeCurrency> = this.addEnumProperty("referenceCurrency", CodeCurrency::class.java)
    private val _inflationRate: BaseProperty<BigDecimal> = this.addBaseProperty("inflationRate", BigDecimal::class.java)
    private val _discountRate: BaseProperty<BigDecimal> = this.addBaseProperty("discountRate", BigDecimal::class.java)
    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private val _logoImage: ReferenceProperty<ObjDocument> = this.addReferenceProperty("logoImage", ObjDocument::class.java)
    private val _mainContact: ReferenceProperty<ObjContact> = this.addReferenceProperty("mainContact", ObjContact::class.java)
    //@formatter:on

    override fun getRepository(): ObjAccountRepository {
        return super.getRepository() as ObjAccountRepository
    }

    // TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
    // override fun aggregate(): ObjAccount = this

    override fun doAfterCreate() {
        super.doAfterCreate()
        check(this.id != null) { "id must not be null after create" }
        this.accountId = this.id as Int
        // TODO-MIGRATION: DMS - uncomment after DMS is migrated
        // this.addLogoImage()
    }

    override fun doBeforeStore() {
        super.doBeforeStore()
        // TODO-MIGRATION: DMS - uncomment after DMS is migrated
        // if (this.getLogoImageId() == null) {
        //     this.addLogoImage()
        // }
    }

    override fun doCalcSearch() {
        super.doCalcSearch()
        this.addSearchText(this.name)
        this.addSearchText(this.description)
    }

    override fun doCalcAll() {
        super.doCalcAll()
        this.calcCaption()
    }

    private fun calcCaption() {
        this.caption.value = this.name
    }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private fun addLogoImage() {
    //     val documentRepo = this.getRepository().documentRepository
    //     val image = documentRepo.create(this.tenantId as Int)
    //     image.name = "Logo"
    //     image.contentKind = CodeContentKindEnum.getContentKind("foto")
    //     image.documentKind = CodeDocumentKindEnum.getDocumentKind("standalone")
    //     image.documentCategory = CodeDocumentCategoryEnum.getDocumentCategory("logo")
    //     documentRepo.store(image)
    //     _logoImage.id = image.id
    // }

    // ObjAccount interface implementation

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

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getLogoImageId(): Int? = _logoImage.id as? Int

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getLogoImage(): ObjDocument? = _logoImage.value

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

