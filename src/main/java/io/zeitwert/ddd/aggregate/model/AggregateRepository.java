
package io.zeitwert.ddd.aggregate.model;

import java.util.List;

import org.jooq.Record;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.session.model.RequestContext;

/**
 * A DDD Aggregate Repository
 */
public interface AggregateRepository<A extends Aggregate, V extends Record> {

	/**
	 * Get aggregate type
	 */
	CodeAggregateType getAggregateType();

	/**
	 * Create a new Aggregate instance
	 * 
	 * @param tenantId   the tenant in which to create the instance (could be
	 *                   different from session, e.g. new Tenant in
	 *                   Kernel-Admin-Session)
	 * @param requestCtx the requestCtx
	 */
	A create(Integer tenantId, RequestContext requestCtx);

	/**
	 * Lookup an Aggregate with given id
	 * return aggregate
	 * throws NoDataFound exception when aggregate not found
	 */
	A get(RequestContext requestCtx, Integer id);

	/**
	 * Discard/rollback in-memory changes to Aggregate
	 */
	void discard(A aggregate);

	/**
	 * Store the Aggregate
	 */
	// @Transactional(propagation = Propagation.MANDATORY) TODO
	void store(A aggregate);

	/**
	 * Find list of Aggregates matching search criteria
	 */
	List<V> find(RequestContext requestCtx, QuerySpec querySpec);

	/**
	 * Get a list of Aggregates with the given foreign key pointing to targetId
	 */
	List<V> getByForeignKey(RequestContext requestCtx, String fkName, Integer targetId);

}
