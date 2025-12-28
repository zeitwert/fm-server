package io.zeitwert.fm.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.delegate.referenceSetProperty
import dddrive.ddd.property.model.ReferenceSetProperty
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole

class ObjUserImpl(
	override val repository: ObjUserRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjUser {

	override var email: String? by baseProperty(this, "email")
	override var name: String? by baseProperty(this, "name")
	override var description: String? by baseProperty(this, "description")
	override var password: String? by baseProperty(this, "password")

	override var needPasswordChange: Boolean? by baseProperty(this, "needPasswordChange")
	override var role: CodeUserRole? by enumProperty(this, "role")
	override var avatarImageId: Any? by referenceIdProperty<ObjDocument>(this, "avatarImage")
	override val avatarImage: ObjDocument? by referenceProperty(this, "avatarImage")
	override val tenantSet: ReferenceSetProperty<ObjTenant> = referenceSetProperty(this, "tenantSet")

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
