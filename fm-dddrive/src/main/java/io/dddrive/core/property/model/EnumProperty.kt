package io.dddrive.core.property.model

import io.dddrive.core.enums.model.Enumerated
import io.dddrive.core.enums.model.Enumeration

interface EnumProperty<E : Enumerated> : BaseProperty<E> {

	val enumeration: Enumeration<E>

	var id: String?

	val idProperty: BaseProperty<String>

	override var value: E?

}
