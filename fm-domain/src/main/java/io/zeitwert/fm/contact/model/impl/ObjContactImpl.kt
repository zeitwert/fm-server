package io.zeitwert.fm.contact.model.impl

import dddrive.ddd.core.model.Part
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.model.PartListProperty
import dddrive.ddd.property.model.Property
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin
import java.time.LocalDate

/** Implementation class for ObjContact using delegation-based property framework. */
class ObjContactImpl(
	override val repository: ObjContactRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjContact,
	AggregateWithNotesMixin,
	AggregateWithTasksMixin {

	override var contactRole: CodeContactRole? by enumProperty(this, "contactRole")
	override var salutation: CodeSalutation? by enumProperty(this, "salutation")
	override var title: CodeTitle? by enumProperty(this, "title")
	override var firstName: String? by baseProperty(this, "firstName")
	override var lastName: String? by baseProperty(this, "lastName")
	override var birthDate: LocalDate? by baseProperty(this, "birthDate")
	override var phone: String? by baseProperty(this, "phone")
	override var mobile: String? by baseProperty(this, "mobile")
	override var email: String? by baseProperty(this, "email")
	override var description: String? by baseProperty(this, "description")
	override val mailAddressList: PartListProperty<ObjContactPartAddress> = partListProperty(this, "mailAddressList")
	override val electronicAddressList: PartListProperty<ObjContactPartAddress> = partListProperty(
		this,
		"electronicAddressList",
	)

	// ItemWithAccount implementation
	override val account
		get() = if (accountId != null) directory.getRepository(ObjAccount::class.java).get(accountId!!) else null

	override fun aggregate(): ObjContact = this

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = directory.getRepository(DocTask::class.java) as DocTaskRepository

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === mailAddressList || property === electronicAddressList) {
			return directory.getPartRepository(ObjContactPartAddress::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
	}

	private fun calcCaption() {
		setCaption("${firstName ?: ""} ${lastName ?: ""}".trim())
	}
}
