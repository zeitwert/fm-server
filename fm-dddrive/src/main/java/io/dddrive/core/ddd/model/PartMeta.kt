package io.dddrive.core.ddd.model

interface PartMeta<A : Aggregate> : EntityMeta {

	val aggregate: A

	val repository: PartRepository<A, out Part<A>>

}
