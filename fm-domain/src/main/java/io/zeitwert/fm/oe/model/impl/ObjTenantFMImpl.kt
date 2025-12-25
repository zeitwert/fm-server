package io.zeitwert.fm.oe.model.impl

import io.dddrive.path.setValueByPath
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
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

open class ObjTenantFMImpl(
	override val repository: ObjTenantFMRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTenantFM {

	// Properties from ObjTenant interface
	override var key: String? by baseProperty()
	override var name: String? by baseProperty()
	override var description: String? by baseProperty()

	// Properties from ObjTenantFM interface
	override var tenantType: CodeTenantType? by enumProperty()
	override var inflationRate: BigDecimal? by baseProperty()
	override var discountRate: BigDecimal? by baseProperty()

	// Reference properties for logo image
	override var logoImageId: Any? by referenceIdProperty<ObjDocument>()
	override val logoImage: ObjDocument? by referenceProperty()

	override val users: List<ObjUserFM>
		get() = repository.userRepository
			.getByForeignKey("tenantId", this.id)
			.map { repository.userRepository.get(it) }

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(name ?: "Tenant")
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		setValueByPath("tenantId", id)
		this.addLogoImage(userId, timestamp)
	}

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doBeforeStore(userId, timestamp)
		if (this.logoImageId == null) {
			this.addLogoImage(userId, timestamp)
		}
	}

	private fun addLogoImage(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		val documentRepo = this.repository.documentRepository
		val image = documentRepo.create(this.tenantId, userId, timestamp)
		image.name = "Logo"
		image.contentKind = CodeContentKind.getContentKind("foto")
		image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
		image.documentCategory = CodeDocumentCategory.getDocumentCategory("logo")
		documentRepo.store(image, userId, timestamp)
		logoImageId = image.id
	}

}

