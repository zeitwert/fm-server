package io.zeitwert.fm.account.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
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
import io.zeitwert.fm.obj.model.base.FMObjBase
import java.math.BigDecimal

class ObjAccountImpl(
	override val repository: ObjAccountRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjAccount,
	AggregateWithNotesMixin {

	// Base properties
	override var name: String? by baseProperty(this, "name")
	override var description: String? by baseProperty(this, "description")
	override var inflationRate: BigDecimal? by baseProperty(this, "inflationRate")
	override var discountRate: BigDecimal? by baseProperty(this, "discountRate")

	// Enum properties
	override var accountType: CodeAccountType? by enumProperty(this, "accountType")
	override var clientSegment: CodeClientSegment? by enumProperty(this, "clientSegment")
	override var referenceCurrency: CodeCurrency? by enumProperty(this, "referenceCurrency")

	// Reference properties - logoImage
	override var logoImageId: Any? by referenceIdProperty<ObjDocument>(this, "logoImage")
	override val logoImage: ObjDocument? by referenceProperty(this, "logoImage")

	// Reference properties - mainContact
	override var mainContactId: Any? by referenceIdProperty<ObjContact>(this, "mainContact")
	override val mainContact: ObjContact? by referenceProperty(this, "mainContact")

	override fun aggregate(): ObjAccount = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		setValueByPath("accountId", id)
		addLogoImage()
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		if (logoImageId == null) {
			addLogoImage()
		}
	}

	override val contactList: List<Any>
		get() {
			return directory.getRepository(ObjContact::class.java).getByForeignKey("accountId", id)
		}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
	}

	private fun calcCaption() {
		setCaption(name)
	}

	private fun addLogoImage() {
		val documentRepo = directory.getRepository(ObjDocument::class.java)
		val image = documentRepo.create()
		image.name = "Logo"
		image.contentKind = CodeContentKind.FOTO
		image.documentKind = CodeDocumentKind.STANDALONE
		image.documentCategory = CodeDocumentCategory.LOGO
		documentRepo.store(image)
		logoImageId = image.id
	}

}
