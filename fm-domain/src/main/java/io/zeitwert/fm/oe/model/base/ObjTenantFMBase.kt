package io.zeitwert.fm.oe.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import java.math.BigDecimal

/**
 * Base class for ObjTenantFM using the NEW dddrive framework.
 */
abstract class ObjTenantFMBase(
    repository: ObjTenantFMRepository
) : FMObjCoreBase(repository), ObjTenantFM {

    //@formatter:off
    private val _tenantType: EnumProperty<CodeTenantType> = this.addEnumProperty("tenantType", CodeTenantType::class.java)
    private val _inflationRate: BaseProperty<BigDecimal> = this.addBaseProperty("inflationRate", BigDecimal::class.java)
    private val _discountRate: BaseProperty<BigDecimal> = this.addBaseProperty("discountRate", BigDecimal::class.java)
    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private val _logoImage: ReferenceProperty<ObjDocument> = this.addReferenceProperty("logoImage", ObjDocument::class.java)
    private val _key: BaseProperty<String> = this.addBaseProperty("key", String::class.java)
    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
    //@formatter:on

    override fun getRepository(): ObjTenantFMRepository {
        return super.getRepository() as ObjTenantFMRepository
    }

    override fun doCalcAll() {
        super.doCalcAll()
        this.calcCaption()
    }

    private fun calcCaption() {
        this.caption.value = _name.value ?: "Tenant"
    }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun doAfterCreate() {
    //     super.doAfterCreate()
    //     this.addLogoImage()
    // }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun doBeforeStore() {
    //     super.doBeforeStore()
    //     if (this.getLogoImageId() == null) {
    //         this.addLogoImage()
    //     }
    // }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private fun addLogoImage() {
    //     val documentRepo = this.getRepository().getDocumentRepository()
    //     val image = documentRepo.create(this.tenantId)
    //     image.name = "Logo"
    //     image.contentKind = CodeContentKindEnum.getContentKind("foto")
    //     image.documentKind = CodeDocumentKindEnum.getDocumentKind("standalone")
    //     image.documentCategory = CodeDocumentCategoryEnum.getDocumentCategory("logo")
    //     documentRepo.store(image)
    //     _logoImage.id = image.id
    // }

    // ObjTenantFM interface implementation

    override fun getTenantType(): CodeTenantType? = _tenantType.value

    override fun setTenantType(tenantType: CodeTenantType?) {
        _tenantType.value = tenantType
    }

    fun getTenantTypeId(): String? = _tenantType.value?.id

    override fun getInflationRate(): BigDecimal? = _inflationRate.value

    override fun setInflationRate(rate: BigDecimal?) {
        _inflationRate.value = rate
    }

    override fun getDiscountRate(): BigDecimal? = _discountRate.value

    override fun setDiscountRate(rate: BigDecimal?) {
        _discountRate.value = rate
    }

    override fun getUsers(): List<ObjUserFM> {
        val userRepo = getRepository().userRepository
        return userRepo.getByForeignKey("tenantId", this.id)
            .filterIsInstance<ObjUserFM>()
    }

    // TODO-MIGRATION: Account - uncomment after Account is migrated
    // override fun getAccounts(): List<ObjAccount> {
    //     val accountRepo = getRepository().getAccountRepository()
    //     return accountRepo.getByForeignKey("tenantId", this.id)
    //         .filterIsInstance<ObjAccount>()
    // }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getLogoImageId(): Int? = _logoImage.id as? Int

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getLogoImage(): ObjDocument? {
    //     val id = _logoImage.id ?: return null
    //     return getRepository().getDocumentRepository().get(id) as? ObjDocument
    // }

    // ObjTenant interface implementation (from core framework)

    override fun getKey(): String? = _key.value

    override fun setKey(key: String?) {
        _key.value = key
    }

    override fun getName(): String? = _name.value

    override fun setName(name: String?) {
        _name.value = name
    }

    override fun getDescription(): String? = _description.value

    override fun setDescription(description: String?) {
        _description.value = description
    }
}

