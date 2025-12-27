package dddrive.ddd.property.model.impl

import dddrive.ddd.core.model.Part

fun interface PartResolver<P : Part<*>> {

	fun get(id: Int): P

}
