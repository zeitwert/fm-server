package io.zeitwert.fm.contact.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
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
	override val repository: ObjContactRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjContact,
	AggregateWithNotesMixin {

	private lateinit var _addressList: PartListProperty<ObjContactPartAddress>

	override fun aggregate(): ObjContact = this

	override fun doInit() {
		super.doInit()
		addEnumProperty("contactRole", CodeContactRole::class.java)
		addEnumProperty("salutation", CodeSalutation::class.java)
		addEnumProperty("title", CodeTitle::class.java)
		addBaseProperty("firstName", String::class.java)
		addBaseProperty("lastName", String::class.java)
		addBaseProperty("birthDate", LocalDate::class.java)
		addBaseProperty("phone", String::class.java)
		addBaseProperty("mobile", String::class.java)
		addBaseProperty("email", String::class.java)
		addBaseProperty("description", String::class.java)
		addReferenceProperty("account", ObjAccount::class.java)
		_addressList = addPartListProperty("addressList", ObjContactPartAddress::class.java)
	}

	override val mailAddressList get() = _addressList.parts.filter { it.isMailAddress == true }

	override fun getMailAddress(addressId: Int) = Optional.ofNullable(_addressList.parts.find { it.id == addressId && it.isMailAddress == true })

	override fun clearMailAddressList() = mailAddressList.forEach { _addressList.removePart(it.id) }

	override fun addMailAddress() = _addressList.addPart(null)

	override fun removeMailAddress(addressId: Int) = _addressList.removePart(addressId)

	override val electronicAddressList get() = _addressList.parts.filter { it.isMailAddress == false }

	override fun getElectronicAddress(addressId: Int) = Optional.ofNullable(_addressList.parts.find { it.id == addressId && it.isMailAddress == false })

	override fun clearElectronicAddressList() = electronicAddressList.forEach { _addressList.removePart(it.id) }

	override fun addElectronicAddress() = _addressList.addPart(null)

	override fun removeElectronicAddress(addressId: Int) = _addressList.removePart(addressId)

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this._addressList) {
			return directory.getPartRepository(ObjContactPartAddress::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		doCalcSearch()
	}

	private fun calcCaption() {
		setCaption("${firstName ?: ""} ${lastName ?: ""}".trim())
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

}
