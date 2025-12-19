package io.zeitwert.fm.account.model.base

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
	repository: ObjAccountRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjAccount,
	AggregateWithNotesMixin {

	override fun aggregate(): ObjAccount = this

	// @formatter:off
	private val _name = addBaseProperty("name", String::class.java)
	private val _description = addBaseProperty("description", String::class.java)
	private val _accountType = addEnumProperty("accountType", CodeAccountType::class.java)
	private val _clientSegment = addEnumProperty("clientSegment", CodeClientSegment::class.java)
	private val _referenceCurrency = addEnumProperty("referenceCurrency", CodeCurrency::class.java)
	private val _inflationRate = addBaseProperty("inflationRate", BigDecimal::class.java)
	private val _discountRate = addBaseProperty("discountRate", BigDecimal::class.java)
	private val _logoImage = addReferenceProperty("logoImage", ObjDocument::class.java)
	private val _mainContact = addReferenceProperty("mainContact", ObjContact::class.java)
	// @formatter:on

	override val repository: ObjAccountRepository = super.repository as ObjAccountRepository

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		check(this._id.value != null) { "id must not be null after create" }
		this.accountId = this._id.value as Int
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

	// override fun doCalcSearch() {
	//     super.doCalcSearch()
	//     this.addSearchText(this.name)
	//     this.addSearchText(this.description)
	// }

	override fun doCalcAll() {
		super.doCalcAll()
		this.calcCaption()
	}

	private fun calcCaption() {
		this._caption.value = this.name
	}

	private fun addLogoImage(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		val documentRepo = this.repository.documentRepository
		val image = documentRepo.create(this.tenantId, userId, timestamp)
// 		image.accountId = this.id.value
		image.name = "Logo"
		image.contentKind = CodeContentKind.FOTO
		image.documentKind = CodeDocumentKind.STANDALONE
		image.documentCategory = CodeDocumentCategory.LOGO
		documentRepo.store(image, userId, timestamp)
		_logoImage.id = image.id
	}

}
