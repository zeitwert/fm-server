package io.dddrive.core.property.model.impl

import io.dddrive.core.enums.model.Enumerated

fun interface EnumResolver<E : Enumerated> {

	fun get(id: String): E

}
