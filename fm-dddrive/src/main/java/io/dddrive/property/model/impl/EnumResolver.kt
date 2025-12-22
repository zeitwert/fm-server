package io.dddrive.property.model.impl

import io.dddrive.enums.model.Enumerated

fun interface EnumResolver<E : Enumerated> {

	fun get(id: String): E

}
