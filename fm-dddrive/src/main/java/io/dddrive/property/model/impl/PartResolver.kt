package io.dddrive.property.model.impl

import io.dddrive.ddd.model.Part

fun interface PartResolver<P : Part<*>> {

	fun get(id: Int): P

}
