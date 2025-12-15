package io.zeitwert.fm.oe.model.base

import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.dddrive.core.property.model.ReferenceSetProperty
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import java.time.OffsetDateTime

abstract class ObjUserFMBase(
	repository: ObjUserFMRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjUserFM {

	private val _avatarImage: ReferenceProperty<ObjDocument> =
		this.addReferenceProperty("avatarImage", ObjDocument::class.java)
	private val _role: EnumProperty<CodeUserRole> = this.addEnumProperty("role", CodeUserRole::class.java)
	private val _tenantSet: ReferenceSetProperty<ObjTenant> =
		this.addReferenceSetProperty("tenantSet", ObjTenant::class.java)
	private val _needPasswordChange: BaseProperty<Boolean> =
		this.addBaseProperty("needPasswordChange", Boolean::class.java)
	private val _password: BaseProperty<String> = this.addBaseProperty("password", String::class.java)
	private val _email: BaseProperty<String> = this.addBaseProperty("email", String::class.java)
	private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
	private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)

	override fun getRepository(): ObjUserFMRepository = super.getRepository() as ObjUserFMRepository

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this.caption.value = _name.value ?: "User"
	}

	override fun doAfterCreate(
		userId: Any?,
		timestamp: OffsetDateTime?,
	) {
		super.doAfterCreate(userId, timestamp)
		this.addAvatarImage(userId, timestamp)
	}

	override fun doBeforeStore(
		userId: Any?,
		timestamp: OffsetDateTime?,
	) {
		super.doBeforeStore(userId, timestamp)
		if (this.getAvatarImageId() == null) {
			this.addAvatarImage(userId, timestamp)
		}
	}

	private fun addAvatarImage(
		userId: Any?,
		timestamp: OffsetDateTime?,
	) {
		val documentRepo = this.getRepository().documentRepository
		val image = documentRepo.create(this.tenantId, userId, timestamp)
		image.name = "Avatar"
		image.contentKind = CodeContentKind.getContentKind("foto")
		image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
		image.documentCategory = CodeDocumentCategory.getDocumentCategory("avatar")
		documentRepo.store(image, userId, timestamp)
		_avatarImage.id = image.id
	}

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

	override fun getAvatarImageId(): Int? = _avatarImage.id as? Int

	override fun getAvatarImage(): ObjDocument? {
		val id = _avatarImage.id ?: return null
		return getRepository().documentRepository.get(id) as? ObjDocument
	}

	override fun getTenantSet(): Set<ObjTenant> =
		_tenantSet
			.getItems()
			.mapNotNull { itemId -> getRepository().get(itemId) as? ObjTenant }
			.toSet()

	override fun addTenant(tenant: ObjTenant) {
		_tenantSet.addItem(tenant.id)
	}

	override fun removeTenant(tenant: ObjTenant) {
		_tenantSet.removeItem(tenant.id)
	}

	override fun clearTenantSet() {
		_tenantSet.clearItems()
	}

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
