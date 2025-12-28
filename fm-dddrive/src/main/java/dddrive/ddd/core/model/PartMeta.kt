package dddrive.ddd.core.model

import dddrive.ddd.property.model.Property

interface PartMeta<A : Aggregate> : EntityMeta {

	val aggregate: A

	val repository: PartRepository<A, out Part<A>>

	val parentProperty: Property<*>

}
