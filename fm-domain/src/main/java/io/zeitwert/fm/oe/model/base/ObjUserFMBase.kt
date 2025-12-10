package io.zeitwert.fm.oe.model.base

import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceSetProperty
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole

/**
 * Base class for ObjUserFM using the NEW dddrive framework.
 */
abstract class ObjUserFMBase(
    repository: ObjUserFMRepository
) : FMObjCoreBase(repository), ObjUserFM {

    //@formatter:off
    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private val _avatarImage: ReferenceProperty<ObjDocument> = this.addReferenceProperty("avatarImage", ObjDocument::class.java)
    private val _role: EnumProperty<CodeUserRole> = this.addEnumProperty("role", CodeUserRole::class.java)
    private val _tenantSet: ReferenceSetProperty<ObjTenant> = this.addReferenceSetProperty("tenantSet", ObjTenant::class.java)
    private val _needPasswordChange: BaseProperty<Boolean> = this.addBaseProperty("needPasswordChange", Boolean::class.java)
    private val _password: BaseProperty<String> = this.addBaseProperty("password", String::class.java)
    private val _email: BaseProperty<String> = this.addBaseProperty("email", String::class.java)
    private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
    //@formatter:on

    override fun getRepository(): ObjUserFMRepository {
        return super.getRepository() as ObjUserFMRepository
    }

    override fun doCalcAll() {
        super.doCalcAll()
        this.calcCaption()
    }

    private fun calcCaption() {
        this.caption.value = _name.value ?: "User"
    }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun doAfterCreate() {
    //     super.doAfterCreate()
    //     this.addAvatarImage()
    // }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun doBeforeStore() {
    //     super.doBeforeStore()
    //     if (this.getAvatarImageId() == null) {
    //         this.addAvatarImage()
    //     }
    // }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // private fun addAvatarImage() {
    //     val documentRepo = this.getRepository().getDocumentRepository()
    //     val image = documentRepo.create(this.tenantId)
    //     image.name = "Avatar"
    //     image.contentKind = CodeContentKindEnum.getContentKind("foto")
    //     image.documentKind = CodeDocumentKindEnum.getDocumentKind("standalone")
    //     image.documentCategory = CodeDocumentCategoryEnum.getDocumentCategory("avatar")
    //     documentRepo.store(image)
    //     _avatarImage.id = image.id
    // }

    // ObjUserFM interface implementation

    override fun getRole(): CodeUserRole? = _role.value

    override fun hasRole(role: CodeUserRole): Boolean = this.getRole() == role

    override fun setRole(role: CodeUserRole?) {
        _role.value = role
    }

    fun getRoleId(): String? = _role.value?.id

    override fun isAppAdmin(): Boolean = this.getRepository().isAppAdmin(this)

    override fun isAdmin(): Boolean = this.getRepository().isAdmin(this)

    override fun getNeedPasswordChange(): Boolean? = _needPasswordChange.value

    override fun setNeedPasswordChange(needPasswordChange: Boolean?) {
        _needPasswordChange.value = needPasswordChange
    }

    override fun getPassword(): String? = _password.value

    override fun setPassword(password: String?) {
        _password.value = password?.let { getRepository().passwordEncoder.encode(it) }
    }

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getAvatarImageId(): Int? = _avatarImage.id as? Int

    // TODO-MIGRATION: DMS - uncomment after DMS is migrated
    // override fun getAvatarImage(): ObjDocument? {
    //     val id = _avatarImage.id ?: return null
    //     return getRepository().getDocumentRepository().get(id) as? ObjDocument
    // }

    override fun getTenantSet(): Set<ObjTenant> {
        return _tenantSet.getItems()
            .mapNotNull { itemId -> getRepository().get(itemId) as? ObjTenant }
            .toSet()
    }

    override fun addTenant(tenant: ObjTenant) {
        _tenantSet.addItem(tenant.id)
    }

    override fun removeTenant(tenant: ObjTenant) {
        _tenantSet.removeItem(tenant.id)
    }

    override fun clearTenantSet() {
        _tenantSet.clearItems()
    }

    // ObjUser interface implementation (from core framework)

    override fun getEmail(): String? = _email.value

    override fun setEmail(email: String?) {
        _email.value = email
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

