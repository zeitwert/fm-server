package io.zeitwert.fm.oe.model.base

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
	repository: ObjTenantFMRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTenantFM {

	private val _tenantType = addEnumProperty("tenantType", CodeTenantType::class.java)
	private val _inflationRate = addBaseProperty("inflationRate", BigDecimal::class.java)
	private val _discountRate = addBaseProperty("discountRate", BigDecimal::class.java)
	private val _logoImage = addReferenceProperty("logoImage", ObjDocument::class.java)
	private val _key = addBaseProperty("key", String::class.java)
	private val _name = addBaseProperty("name", String::class.java)
	private val _description = addBaseProperty("description", String::class.java)

	override val repository get() = super.repository as ObjTenantFMRepository

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this._caption.value = _name.value ?: "Tenant"
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
		_logoImage.id = image.id
	}

	override val users: List<ObjUserFM>
		get() = repository.userRepository
			.getByForeignKey("tenantId", this.id)
			.map { repository.userRepository.get(it) }

	override val logoImageId get() = _logoImage.id

	override val logoImage get() = if (_logoImage.id != null) repository.documentRepository.get(id) else null

}
