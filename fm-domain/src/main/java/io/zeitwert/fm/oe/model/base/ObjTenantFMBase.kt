package io.zeitwert.fm.oe.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import java.math.BigDecimal
import java.time.OffsetDateTime

abstract class ObjTenantFMBase(
    repository: ObjTenantFMRepository
) : FMObjBase(repository), ObjTenantFM {

    private val _tenantType: EnumProperty<CodeTenantType> = this.addEnumProperty("tenantType", CodeTenantType::class.java)
    private val _inflationRate: BaseProperty<BigDecimal> = this.addBaseProperty("inflationRate", BigDecimal::class.java)
    private val _discountRate: BaseProperty<BigDecimal> = this.addBaseProperty("discountRate", BigDecimal::class.java)
    private val _logoImage: ReferenceProperty<ObjDocument> = this.addReferenceProperty("logoImage", ObjDocument::class.java)
    private val _key: BaseProperty<String> = this.addBaseProperty("key", String::class.java)
    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)

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

    override fun doAfterCreate(userId: Any?, timestamp: OffsetDateTime?) {
        super.doAfterCreate(userId, timestamp)
        this.addLogoImage(userId, timestamp)
    }

    override fun doBeforeStore(userId: Any?, timestamp: OffsetDateTime?) {
        super.doBeforeStore(userId, timestamp)
        if (this.getLogoImageId() == null) {
            this.addLogoImage(userId, timestamp)
        }
    }

    private fun addLogoImage(userId: Any?, timestamp: OffsetDateTime?) {
        val documentRepo = this.getRepository().documentRepository
        val image = documentRepo.create(this.tenantId, userId, timestamp)
        image.name = "Logo"
        image.contentKind = CodeContentKind.getContentKind("foto")
        image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
        image.documentCategory = CodeDocumentCategory.getDocumentCategory("logo")
        documentRepo.store(image, userId, timestamp)
        _logoImage.id = image.id
    }

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

    override fun getLogoImageId(): Int? = _logoImage.id as? Int

    override fun getLogoImage(): ObjDocument? {
        val id = _logoImage.id ?: return null
        return getRepository().documentRepository.get(id) as? ObjDocument
    }

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
