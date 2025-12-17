package io.dddrive.core.property.model.impl

import io.dddrive.core.ddd.model.Part

fun interface PartResolver<P : Part<*>> {

	fun get(id: Int): P

}
