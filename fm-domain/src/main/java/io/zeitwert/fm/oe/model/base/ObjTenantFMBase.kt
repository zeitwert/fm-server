package io.zeitwert.fm.oe.model.base

import io.dddrive.path.setValueByPath
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
	override val repository: ObjTenantFMRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTenantFM {

	override fun doInit() {
		super.doInit()
		addEnumProperty("tenantType", CodeTenantType::class.java)
		addBaseProperty("inflationRate", BigDecimal::class.java)
		addBaseProperty("discountRate", BigDecimal::class.java)
		addReferenceProperty("logoImage", ObjDocument::class.java)
		addBaseProperty("key", String::class.java)
		addBaseProperty("name", String::class.java)
		addBaseProperty("description", String::class.java)
	}

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
		setValueByPath("logoImageId", image.id)
	}

	override val users: List<ObjUserFM>
		get() = repository.userRepository
			.getByForeignKey("tenantId", this.id)
			.map { repository.userRepository.get(it) }

}
