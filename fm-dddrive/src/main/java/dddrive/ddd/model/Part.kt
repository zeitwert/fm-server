package dddrive.ddd.model

/**
 * A Part is an Entity that belongs to an Aggregate (but might be attached to another part as
 * parent).
 */
interface Part<A : Aggregate> : Entity<Int> {

	override val id: Int

	val meta: PartMeta<A>

}
