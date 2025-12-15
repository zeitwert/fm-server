package io.zeitwert.fm.contact.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.base.ObjPartBase
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.Property
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeAddressType
import io.zeitwert.fm.oe.model.enums.CodeCountry

/**
 * Base class for ObjContactPartAddress using the NEW dddrive framework.
 */
abstract class ObjContactPartAddressBase(
	obj: ObjContact,
	repository: PartRepository<ObjContact, out Part<ObjContact>>,
	property: Property<*>,
	id: Int?,
) : ObjPartBase<ObjContact>(obj, repository, property, id),
	ObjContactPartAddress {

	// @formatter:off
	private val _addressChannel: EnumProperty<CodeAddressChannel> = this.addEnumProperty("addressChannel", CodeAddressChannel::class.java)
	private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
	private val _street: BaseProperty<String> = this.addBaseProperty("street", String::class.java)
	private val _zip: BaseProperty<String> = this.addBaseProperty("zip", String::class.java)
	private val _city: BaseProperty<String> = this.addBaseProperty("city", String::class.java)
	private val _country: EnumProperty<CodeCountry> = this.addEnumProperty("country", CodeCountry::class.java)
	// @formatter:on

	// ObjContactPartAddress interface implementation

	override fun getAddressType(): CodeAddressType? = _addressChannel.value?.addressType

	override fun getIsMailAddress(): Boolean? = _addressChannel.value?.isMailAddress

	override fun getAddressChannel(): CodeAddressChannel? = _addressChannel.value

	override fun setAddressChannel(addressChannel: CodeAddressChannel?) {
		_addressChannel.value = addressChannel
	}

	override fun getName(): String? = _name.value

	override fun setName(name: String?) {
		_name.value = name
	}

	override fun getStreet(): String? = _street.value

	override fun setStreet(street: String?) {
		_street.value = street
	}

	override fun getZip(): String? = _zip.value

	override fun setZip(zip: String?) {
		_zip.value = zip
	}

	override fun getCity(): String? = _city.value

	override fun setCity(city: String?) {
		_city.value = city
	}

	override fun getCountry(): CodeCountry? = _country.value

	override fun setCountry(country: CodeCountry?) {
		_country.value = country
	}

}
