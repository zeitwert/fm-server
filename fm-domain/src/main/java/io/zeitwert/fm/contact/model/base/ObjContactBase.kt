package io.zeitwert.fm.contact.model.base

import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.obj.model.base.FMObjBase
import java.time.LocalDate
import java.util.*

/**
 * Base class for ObjContact using the NEW dddrive framework.
 */
abstract class ObjContactBase(
	repository: ObjContactRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjContact,
	AggregateWithNotesMixin {

	override fun aggregate(): ObjContact = this

	private val _contactRole = addEnumProperty("contactRole", CodeContactRole::class.java)
	private val _salutation = addEnumProperty("salutation", CodeSalutation::class.java)
	private val _title = addEnumProperty("title", CodeTitle::class.java)
	private val _firstName = addBaseProperty("firstName", String::class.java)
	private val _lastName = addBaseProperty("lastName", String::class.java)
	private val _birthDate = addBaseProperty("birthDate", LocalDate::class.java)
	private val _phone = addBaseProperty("phone", String::class.java)
	private val _mobile = addBaseProperty("mobile", String::class.java)
	private val _email = addBaseProperty("email", String::class.java)
	private val _description = addBaseProperty("description", String::class.java)
	private val _addressList = addPartListProperty("addressList", ObjContactPartAddress::class.java)
	private val _account = addReferenceProperty("account", ObjAccount::class.java)

	override val repository get() = super.repository as ObjContactRepository

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		doCalcSearch()
	}

	private fun calcCaption() {
		this._caption.value = "${firstName ?: ""} ${lastName ?: ""}".trim()
	}

	private fun doCalcSearch() {
		//     addSearchToken(getFirstName())
		//     addSearchToken(getLastName())
		//     addSearchToken(getEmail())
		//     getEmail()?.let { email ->
		//         addSearchText(email.replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "))
		//     }
		//     addSearchText(getDescription())
	}

	override val account get() = _account.value

	override val mailAddressList = _addressList.parts.filter { it.isMailAddress == true }

	override fun getMailAddress(addressId: Int) =
		Optional.ofNullable(_addressList.parts.find { it.id == addressId && it.isMailAddress == true })

	override fun clearMailAddressList() = mailAddressList.forEach { _addressList.removePart(it.id) }

	override fun addMailAddress() = _addressList.addPart(null)

	override fun removeMailAddress(addressId: Int) = _addressList.removePart(addressId)

	override val electronicAddressList = _addressList.parts.filter { it.isMailAddress == false }

	override fun getElectronicAddress(addressId: Int) =
		Optional.ofNullable(_addressList.parts.find { it.id == addressId && it.isMailAddress == false })

	override fun clearElectronicAddressList() = electronicAddressList.forEach { _addressList.removePart(it.id) }

	override fun addElectronicAddress() = _addressList.addPart(null)

	override fun removeElectronicAddress(addressId: Int) = _addressList.removePart(addressId)

}
