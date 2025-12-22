package io.zeitwert.fm.contact.model

import io.dddrive.obj.model.ObjPart
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeAddressType
import io.zeitwert.fm.oe.model.enums.CodeCountry

interface ObjContactPartAddress : ObjPart<ObjContact> {

	val addressType: CodeAddressType?

	val isMailAddress: Boolean?

	var addressChannel: CodeAddressChannel?

	var name: String?

	var street: String?

	var zip: String?

	var city: String?

	var country: CodeCountry?

}
