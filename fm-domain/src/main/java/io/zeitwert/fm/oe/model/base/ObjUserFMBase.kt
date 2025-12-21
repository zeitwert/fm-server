package io.zeitwert.fm.oe.model.base

import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.path.setValueByPath
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
	override val repository: ObjUserFMRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjUserFM {

	override fun doInit() {
		super.doInit()
		addReferenceProperty("avatarImage", ObjDocument::class.java)
		addEnumProperty("role", CodeUserRole::class.java)
		addReferenceSetProperty("tenantSet", ObjTenant::class.java)
		addBaseProperty("needPasswordChange", Boolean::class.java)
		addBaseProperty("password", String::class.java)
		addBaseProperty("email", String::class.java)
		addBaseProperty("name", String::class.java)
		addBaseProperty("description", String::class.java)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(name ?: "User")
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
		setValueByPath("avatarImageId", image.id)
	}

	override val isAppAdmin get() = repository.isAppAdmin(this)

	override val isAdmin get() = repository.isAdmin(this)

	override fun hasRole(role: CodeUserRole) = this.role == role

}
