package io.zeitwert.fm.account.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.property.path.setValueByPath
import dddrive.property.delegate.baseProperty
import dddrive.property.delegate.enumProperty
import dddrive.property.delegate.referenceIdProperty
import dddrive.property.delegate.referenceProperty
import dddrive.query.query
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import java.math.BigDecimal

class ObjAccountImpl(
	override val repository: ObjAccountRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjAccount,
	AggregateWithNotesMixin {

	// Base properties
	override var key by baseProperty<String>("key")
	override var name by baseProperty<String>("name")
	override var description by baseProperty<String>("description")
	override var inflationRate by baseProperty<BigDecimal>("inflationRate")
	override var discountRate by baseProperty<BigDecimal>("discountRate")

	// Enum properties
	override var accountType by enumProperty<CodeAccountType>("accountType")
	override var clientSegment by enumProperty<CodeClientSegment>("clientSegment")
	override var referenceCurrency by enumProperty<CodeCurrency>("referenceCurrency")

	// Reference properties - logoImage
	override var logoImageId by referenceIdProperty<ObjDocument>("logoImage")
	override val logoImage by referenceProperty<ObjDocument>("logoImage")

	// Reference properties - mainContact
	override var mainContactId by referenceIdProperty<ObjContact>("mainContact")
	override val mainContact by referenceProperty<ObjContact>("mainContact")

	override fun aggregate(): ObjAccount = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		setValueByPath("accountId", id)
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		addLogoImage()
	}

	override val contactList: List<Any>
		get() {
			val querySpec = query {
				filter { "accountId" eq id }
			}
			return directory.getRepository(ObjContact::class.java).find(querySpec)
		}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		calcMainContact()
	}

	private fun calcCaption() {
		setCaption(name)
	}

	private fun calcMainContact() {
		mainContactId = contactList.firstOrNull()
	}

	private fun addLogoImage() {
		val documentRepo = directory.getRepository(ObjDocument::class.java)
		val image = documentRepo.create()
		// need to overwrite sessionContext.accountId upon creation
		image.accountId = id
		image.name = "Logo"
		image.contentKind = CodeContentKind.FOTO
		image.documentKind = CodeDocumentKind.STANDALONE
		image.documentCategory = CodeDocumentCategory.LOGO
		documentRepo.store(image)
		logoImageId = image.id
	}

}
