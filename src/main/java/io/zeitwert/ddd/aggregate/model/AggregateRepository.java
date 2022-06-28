
package io.zeitwert.ddd.aggregate.model;

import java.util.List;

import org.jooq.Record;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.session.model.SessionInfo;

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
	 */
	A create(SessionInfo sessionInfo);

	/**
	 * Lookup an Aggregate with given id
	 * return aggregate
	 * throws NoDataFound exception when aggregate not found
	 */
	A get(SessionInfo sessionInfo, Integer id);

	/**
	 * Discard/rollback in-memory changes to Aggregate
	 */
	void discard(A aggregate);

	/**
	 * Store the Aggregate
	 */
	void store(A aggregate);

	/**
	 * Find list of Aggregates matching search criteria
	 */
	List<V> find(SessionInfo sessionInfo, QuerySpec querySpec);

	/**
	 * Get a list of Aggregates with the given foreign key pointing to targetId
	 */
	List<V> getByForeignKey(SessionInfo sessionInfo, String fkName, Integer targetId);

}
