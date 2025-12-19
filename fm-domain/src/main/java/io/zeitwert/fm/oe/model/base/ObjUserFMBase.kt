package io.zeitwert.fm.oe.model.base

import io.dddrive.core.oe.model.ObjTenant
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

	private val _avatarImage = addReferenceProperty("avatarImage", ObjDocument::class.java)
	private val _role = addEnumProperty("role", CodeUserRole::class.java)
	private val _tenantSet = addReferenceSetProperty("tenantSet", ObjTenant::class.java)
	private val _needPasswordChange = addBaseProperty("needPasswordChange", Boolean::class.java)
	private val _password = addBaseProperty("password", String::class.java)
	private val _email = addBaseProperty("email", String::class.java)
	private val _name = addBaseProperty("name", String::class.java)
	private val _description = addBaseProperty("description", String::class.java)

	override val repository get() = super.repository as ObjUserFMRepository

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this._caption.value = _name.value ?: "User"
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		this.addAvatarImage(userId, timestamp)
	}

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doBeforeStore(userId, timestamp)
		if (avatarImageId == null) {
			this.addAvatarImage(userId, timestamp)
		}
	}

	private fun addAvatarImage(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		val documentRepo = repository.documentRepository
		val image = documentRepo.create(this.tenantId, userId, timestamp)
		image.name = "Avatar"
		image.contentKind = CodeContentKind.getContentKind("foto")
		image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
		image.documentCategory = CodeDocumentCategory.getDocumentCategory("avatar")
		documentRepo.store(image, userId, timestamp)
		_avatarImage.id = image.id
	}

	override val isAppAdmin get() = repository.isAppAdmin(this)

	override val isAdmin = repository.isAdmin(this)

	override fun hasRole(role: CodeUserRole) = this.role == role

}
