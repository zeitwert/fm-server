package io.dddrive.core.property.model

import io.dddrive.core.ddd.model.Part

interface PartReferenceProperty<P : Part<*>> : BaseProperty<P> {

	var id: Int?

	val idProperty: BaseProperty<Int>

	override var value: P?

}
