package io.zeitwert.fm.contact.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.ddd.model.PartRepository
import io.dddrive.core.obj.model.base.ObjPartBase
import io.dddrive.core.property.model.Property
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.oe.model.enums.CodeCountry

/**
 * Base class for ObjContactPartAddress using the NEW dddrive framework.
 */
abstract class ObjContactPartAddressBase(
	obj: ObjContact,
	repository: PartRepository<ObjContact, out Part<ObjContact>>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjContact>(obj, repository, property, id),
	ObjContactPartAddress {

	private val _addressChannel = addEnumProperty("addressChannel", CodeAddressChannel::class.java)
	private val _name = addBaseProperty("name", String::class.java)
	private val _street = addBaseProperty("street", String::class.java)
	private val _zip = addBaseProperty("zip", String::class.java)
	private val _city = addBaseProperty("city", String::class.java)
	private val _country = addEnumProperty("country", CodeCountry::class.java)

	override val addressType = _addressChannel.value?.addressType

	override val isMailAddress = _addressChannel.value?.isMailAddress

}
