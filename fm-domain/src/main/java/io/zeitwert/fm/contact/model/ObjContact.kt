package io.zeitwert.fm.contact.model

import io.dddrive.obj.model.Obj
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.task.model.ItemWithTasks
import java.time.LocalDate
import java.util.*

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

	val mailAddressList: List<ObjContactPartAddress>

	fun getMailAddress(addressId: Int): Optional<ObjContactPartAddress>

	fun clearMailAddressList()

	fun addMailAddress(): ObjContactPartAddress

	fun removeMailAddress(addressId: Int)

	val electronicAddressList: List<ObjContactPartAddress>

	fun getElectronicAddress(addressId: Int): Optional<ObjContactPartAddress>

	fun clearElectronicAddressList()

	fun addElectronicAddress(): ObjContactPartAddress

	fun removeElectronicAddress(addressId: Int)

}
