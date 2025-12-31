package dddrive.ddd.core.model

/**
 * A DDD Aggregate Root.
 */
interface Aggregate : Entity<Any> {

	override val id: Any

	val meta: AggregateMeta

}
