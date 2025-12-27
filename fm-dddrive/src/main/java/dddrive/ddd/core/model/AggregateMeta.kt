package dddrive.ddd.core.model

/**
 * A DDD Aggregate Root Meta Information.
 */
interface AggregateMeta : EntityMeta {

	val repository: AggregateRepository<*>

	val version: Int

}
