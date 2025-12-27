package dddrive.ddd.core.model

import dddrive.ddd.core.model.enums.CodeAggregateType
import java.time.OffsetDateTime

/**
 * A DDD Aggregate Repository
 */
interface AggregateRepository<A : Aggregate> {

	/**
	 * Get aggregate type
	 */
	val aggregateType: CodeAggregateType

	/**
	 * Get repository directory
	 */
	val directory: RepositoryDirectory

	/**
	 * Convert id to string
	 */
	fun idToString(id: Any?): String?

	/**
	 * Convert string to id
	 */
	fun idFromString(id: String?): Any?

	/**
	 * Do we fire property change events for this property?
	 */
	fun doLogChange(property: String): Boolean

	/**
	 * Create a new Aggregate instance
	 *
	 * @param tenantId  the tenant in which to create the instance (could be different from session tenant, e.g. when opening a new Tenant in Kernel-Admin-Session)
	 * @param userId    the user that creates the instance
	 * @param timestamp the timestamp of creation
	 */
	fun create(
		tenantId: Any,
		userId: Any,
		timestamp: OffsetDateTime,
	): A

	/**
	 * Get a read-only Aggregate with given id
	 *
	 * @return aggregate, it will be frozen
	 * @throws java.lang.RuntimeException exception when aggregate not found
	 */
	fun get(id: Any): A

	/**
	 * Get a writeable Aggregate with given id
	 *
	 * @return aggregate (not frozen)
	 * @throws java.lang.RuntimeException exception when aggregate not found
	 */
	fun load(id: Any): A

	/**
	 * Store the Aggregate
	 */
	fun store(
		aggregate: A,
		userId: Any,
		timestamp: OffsetDateTime,
	)

	/**
	 * Get a list of Aggregates with the given foreign key pointing to targetId
	 */
	fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any>

}
