package io.zeitwert.fm.oe.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.delegate.referenceProperty
import dddrive.property.path.setValueByPath
import dddrive.query.query
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import java.math.BigDecimal

class ObjTenantImpl(
	override val repository: ObjTenantRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjTenant {

	override var key by baseProperty<String>("key")
	override var name by baseProperty<String>("name")
	override var description by baseProperty<String>("description")

	override var tenantType by enumProperty<CodeTenantType>("tenantType")
	override var inflationRate by baseProperty<BigDecimal>("inflationRate")
	override var discountRate by baseProperty<BigDecimal>("discountRate")
	override var logoImageId by referenceIdProperty<ObjDocument>("logoImage")
	override val logoImage by referenceProperty<ObjDocument>("logoImage")

	override val users: List<ObjUser>
		get() {
			val querySpec = query {
				filter { "tenantId" eq this@ObjTenantImpl.id }
			}
			return repository.userRepository.find(querySpec).map { repository.userRepository.get(it) }
		}

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		setCaption(name ?: "Tenant")
	}

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		setValueByPath("tenantId", id)
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		if (logoImageId == null) {
			this.addLogoImage()
		}
	}

	private fun addLogoImage() {
		val documentRepo = this.repository.documentRepository
		val image = documentRepo.create()
		image.name = "Logo"
		image.contentKind = CodeContentKind.getContentKind("foto")
		image.documentKind = CodeDocumentKind.getDocumentKind("standalone")
		image.documentCategory = CodeDocumentCategory.getDocumentCategory("logo")
		documentRepo.store(image)
		logoImageId = image.id
	}

}
