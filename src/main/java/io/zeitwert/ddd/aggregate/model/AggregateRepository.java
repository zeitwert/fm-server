
package io.zeitwert.ddd.aggregate.model;

import java.util.List;
import java.util.Optional;

import org.jooq.Record;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.session.model.SessionInfo;

/**
 * A DDD Aggregate (Readonly) Repository
 */
public interface AggregateRepository<A extends Aggregate, V extends Record> {

	/**
	 * Get aggregate type
	 */
	CodeAggregateType getAggregateType();

	/**
	 * Lookup an Aggregate with given id
	 */
	Optional<A> get(SessionInfo sessionInfo, Integer id);

	/**
	 * Find list of Aggregates matching search criteria
	 */
	List<V> find(SessionInfo sessionInfo, QuerySpec querySpec);

	/**
	 * Get a list of Aggregates with the given foreign key pointing to targetId
	 */
	List<V> getByForeignKey(SessionInfo sessionInfo, String fkName, Integer targetId);

	/**
	 * Create a new Aggregate instance
	 */
	A create(SessionInfo sessionInfo);

	/**
	 * Store the Aggregate
	 */
	void store(A aggregate);

}
