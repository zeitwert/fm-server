package io.zeitwert.fm.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.delegate.referenceProperty
import dddrive.property.delegate.referenceSetProperty
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole

class ObjUserImpl(
	override val repository: ObjUserRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjUser {

	override var email by baseProperty<String>("email")
	override var name by baseProperty<String>("name")
	override var description by baseProperty<String>("description")
	override var password by baseProperty<String>("password")

	override var needPasswordChange by baseProperty<Boolean>("needPasswordChange")
	override var role by enumProperty<CodeUserRole>("role")
	override var avatarImageId by referenceIdProperty<ObjDocument>("avatarImage")
	override val avatarImage by referenceProperty<ObjDocument>("avatarImage")
	override val tenantSet = referenceSetProperty<ObjTenant>("tenantSet")

	override val isAppAdmin get() = repository.isAppAdmin(this)

	override val isAdmin get() = repository.isAdmin(this)

	override fun hasRole(role: CodeUserRole) = this.role == role

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(name ?: "User")
	}

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		this.addAvatarImage()
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		if (avatarImageId == null) {
			this.addAvatarImage()
		}
	}

	private fun addAvatarImage() {
		val documentRepo = repository.documentRepository
		val image = documentRepo.create()
		image.name = "Avatar"
		image.contentKind = CodeContentKind.getContentKind("foto")
		image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
		image.documentCategory = CodeDocumentCategory.getDocumentCategory("avatar")
		documentRepo.store(image)
		avatarImageId = image.id
	}

}
