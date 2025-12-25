package io.zeitwert.fm.oe.model.impl

import io.dddrive.oe.model.ObjTenant
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.delegate.referenceSetProperty
import io.dddrive.property.model.ReferenceSetProperty
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import java.time.OffsetDateTime

open class ObjUserFMImpl(
	override val repository: ObjUserFMRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjUserFM {

	// Properties from ObjUser interface
	override var email: String? by baseProperty()
	override var name: String? by baseProperty()
	override var description: String? by baseProperty()
	override var password: String? by baseProperty()

	// Properties from ObjUserFM interface
	override var needPasswordChange: Boolean? by baseProperty()

	// Enum property
	override var role: CodeUserRole? by enumProperty()

	// Reference properties for avatar image
	override var avatarImageId: Any? by referenceIdProperty<ObjDocument>()
	override val avatarImage: ObjDocument? by referenceProperty()

	// Reference set property for tenants
	override val tenantSet: ReferenceSetProperty<ObjTenant> by referenceSetProperty()

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
		avatarImageId = image.id
	}

}

