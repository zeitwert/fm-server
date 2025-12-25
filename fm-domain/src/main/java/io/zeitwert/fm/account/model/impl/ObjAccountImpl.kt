package io.zeitwert.fm.account.model.impl

import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
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
import java.time.OffsetDateTime

open class ObjAccountImpl(
	override val repository: ObjAccountRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjAccount,
	AggregateWithNotesMixin {

	// Base properties
	override var name: String? by baseProperty()
	override var description: String? by baseProperty()
	override var inflationRate: BigDecimal? by baseProperty()
	override var discountRate: BigDecimal? by baseProperty()

	// Enum properties
	override var accountType: CodeAccountType? by enumProperty()
	override var clientSegment: CodeClientSegment? by enumProperty()
	override var referenceCurrency: CodeCurrency? by enumProperty()

	// Reference properties - logoImage
	override var logoImageId: Any? by referenceIdProperty<ObjDocument>()
	override val logoImage: ObjDocument? by referenceProperty()

	// Reference properties - mainContact
	override var mainContactId: Any? by referenceIdProperty<ObjContact>()
	override val mainContact: ObjContact? by referenceProperty()

	override fun aggregate(): ObjAccount = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

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

	private fun addLogoImage(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		val documentRepo = repository.documentRepository
		val image = documentRepo.create(tenantId, userId, timestamp)
		image.name = "Logo"
		image.contentKind = CodeContentKind.FOTO
		image.documentKind = CodeDocumentKind.STANDALONE
		image.documentCategory = CodeDocumentCategory.LOGO
		documentRepo.store(image, userId, timestamp)
		logoImageId = image.id
	}

}
