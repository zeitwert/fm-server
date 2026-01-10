package io.zeitwert.fm.contact.model.impl

import dddrive.app.obj.model.base.ObjPartBase
import dddrive.ddd.model.PartRepository
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.model.Property
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.oe.model.enums.CodeCountry

/**
 * Implementation class for ObjContactPartAddress using delegation-based property framework.
 */
class ObjContactPartAddressImpl(
	obj: ObjContact,
	repository: PartRepository<ObjContact, ObjContactPartAddress>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjContact>(obj, repository, property, id),
	ObjContactPartAddress {

	override var addressChannel by enumProperty<CodeAddressChannel>("addressChannel")
	override var name by baseProperty<String>("name")
	override var street by baseProperty<String>("street")
	override var zip by baseProperty<String>("zip")
	override var city by baseProperty<String>("city")
	override var country by enumProperty<CodeCountry>("country")

	override val addressType get() = addressChannel?.addressType

	override val isMailAddress get() = addressChannel?.isMailAddress

}
