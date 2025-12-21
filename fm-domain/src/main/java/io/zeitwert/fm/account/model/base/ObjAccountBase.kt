package io.zeitwert.fm.account.model.base

import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.obj.model.base.FMObjBase
import java.math.BigDecimal
import java.time.OffsetDateTime

abstract class ObjAccountBase(
	override val repository: ObjAccountRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjAccount,
	AggregateWithNotesMixin {

	override fun aggregate(): ObjAccount = this

	override fun doInit() {
		super.doInit()
		addBaseProperty("name", String::class.java)
		addBaseProperty("description", String::class.java)
		addEnumProperty("accountType", CodeAccountType::class.java)
		addEnumProperty("clientSegment", CodeClientSegment::class.java)
		addEnumProperty("referenceCurrency", CodeCurrency::class.java)
		addBaseProperty("inflationRate", BigDecimal::class.java)
		addBaseProperty("discountRate", BigDecimal::class.java)
		addReferenceProperty("logoImage", ObjDocument::class.java)
		addReferenceProperty("mainContact", ObjContact::class.java)
	}

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		check(getValueByPath<Any>("id") != null) { "id must not be null after create" }
		setValueByPath("accountId", id)
		addLogoImage(userId, timestamp)
	}

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doBeforeStore(userId, timestamp)
		if (logoImageId == null) {
			addLogoImage(userId, timestamp)
		}
	}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
	}

	private fun calcCaption() {
		setCaption(name)
	}

	// override fun doCalcSearch() {
	//     super.doCalcSearch()
	//     addSearchText(name)
	//     addSearchText(description)
	// }

	private fun addLogoImage(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		val documentRepo = repository.documentRepository
		val image = documentRepo.create(tenantId, userId, timestamp)
// 		image.accountId = id.value
		image.name = "Logo"
		image.contentKind = CodeContentKind.FOTO
		image.documentKind = CodeDocumentKind.STANDALONE
		image.documentCategory = CodeDocumentCategory.LOGO
		documentRepo.store(image, userId, timestamp)
		setValueByPath("logoImageId", image.id)
	}

}
