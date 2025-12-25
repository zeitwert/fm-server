package io.zeitwert.fm.contact.model.impl

import io.dddrive.ddd.model.PartRepository
import io.dddrive.obj.model.base.ObjPartBase
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.model.Property
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.oe.model.enums.CodeCountry

/**
 * Implementation class for ObjContactPartAddress using delegation-based property framework.
 */
open class ObjContactPartAddressImpl(
	obj: ObjContact,
	repository: PartRepository<ObjContact, ObjContactPartAddress>,
	property: Property<*>,
	id: Int,
) : ObjPartBase<ObjContact>(obj, repository, property, id),
	ObjContactPartAddress {

	// Enum property
	override var addressChannel: CodeAddressChannel? by enumProperty()

	// Base properties
	override var name: String? by baseProperty()
	override var street: String? by baseProperty()
	override var zip: String? by baseProperty()
	override var city: String? by baseProperty()

	// Enum property for country
	override var country: CodeCountry? by enumProperty()

	// Computed properties (no setter, derived from addressChannel)
	override val addressType get() = addressChannel?.addressType

	override val isMailAddress get() = addressChannel?.isMailAddress

}
