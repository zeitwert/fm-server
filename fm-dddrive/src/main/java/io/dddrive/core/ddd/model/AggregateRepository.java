package io.dddrive.core.ddd.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.core.ddd.model.enums.CodeAggregateType;

/**
 * A DDD Aggregate Repository
 */
public interface AggregateRepository<A extends Aggregate> {

	/**
	 * Get aggregate type
	 */
	CodeAggregateType getAggregateType();

	/**
	 * Get repository directory
	 */
	RepositoryDirectory getDirectory();

	/**
	 * Convert id to string
	 */
	String idToString(Object id);

	/**
	 * Convert string to id
	 */
	Object idFromString(String id);

	/**
	 * Do we fire property change events for this property?
	 */
	boolean doLogChange(String property);

	/**
	 * Create a new Aggregate instance
	 *
	 * @param tenantId  the tenant in which to create the instance (could be
	 *                  different from session, e.g. new Tenant in
	 *                  Kernel-Admin-Session)
	 * @param userId    the user that creates the instance
	 * @param timestamp the timestamp of creation
	 */
	A create(Object tenantId, Object userId, OffsetDateTime timestamp);

	/**
	 * Get a read-only Aggregate with given id
	 *
	 * @return aggregate, it will be frozen
	 * @throws java.lang.RuntimeException exception when aggregate not found
	 */
	A get(Object id);

	/**
	 * Get a writeable Aggregate with given id
	 *
	 * @return aggregate (not frozen)
	 * @throws java.lang.RuntimeException exception when aggregate not found
	 */
	A load(Object id);

	/**
	 * Store the Aggregate
	 */
	void store(A aggregate, Object userId, OffsetDateTime timestamp);

	/**
	 * Get all Aggregates (read-only) in the given tenant
	 */
	List<A> getAll(Object tenantId);

	/**
	 * Get a list of Aggregates with the given foreign key pointing to targetId
	 */
	List<A> getByForeignKey(String fkName, Object targetId);

}
