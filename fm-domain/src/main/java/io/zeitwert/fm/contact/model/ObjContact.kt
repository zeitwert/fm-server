package io.zeitwert.fm.contact.model

import io.dddrive.obj.model.Obj
import io.dddrive.property.model.PartListProperty
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.task.model.ItemWithTasks
import java.time.LocalDate

interface ObjContact :
	Obj,
	ItemWithAccount,
	ItemWithNotes,
	ItemWithTasks {

	var contactRole: CodeContactRole?

	var salutation: CodeSalutation?

	var title: CodeTitle?

	var firstName: String?

	var lastName: String?

	var birthDate: LocalDate?

	var phone: String?

	var mobile: String?

	var email: String?

	var description: String?

	val mailAddressList: PartListProperty<ObjContactPartAddress>

	val electronicAddressList: PartListProperty<ObjContactPartAddress>
}
