package io.zeitwert.fm.contact.model.base

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.model.Property
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.oe.model.enums.CodeCountry

/**
 * Base class for ObjContactPartAddress using the NEW dddrive framework.
 */
abstract class ObjContactPartAddressBase(
	obj: ObjContact,
	repository: PartRepository<ObjContact, ObjContactPartAddress>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjContact>(obj, repository, property, id),
	ObjContactPartAddress {

	override fun doInit() {
		super.doInit()
		addEnumProperty("addressChannel", CodeAddressChannel::class.java)
		addBaseProperty("name", String::class.java)
		addBaseProperty("street", String::class.java)
		addBaseProperty("zip", String::class.java)
		addBaseProperty("city", String::class.java)
		addEnumProperty("country", CodeCountry::class.java)
	}

	override val addressType get() = addressChannel?.addressType

	override val isMailAddress get() = addressChannel?.isMailAddress

}
