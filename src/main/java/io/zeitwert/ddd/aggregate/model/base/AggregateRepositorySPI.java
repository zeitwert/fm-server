package io.zeitwert.ddd.aggregate.model.base;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.session.model.SessionInfo;

import java.util.List;

import org.jooq.Record;

/**
 * This class defines the internal callbacks for a AggregateRepository
 * implementation.
 */
public interface AggregateRepositorySPI<A extends Aggregate, V extends Record> {

	/**
	 * Register the required PartRepositories
	 */
	void registerPartRepositories();

	/**
	 * Provide a new Aggregate id
	 * 
	 * @return new aggregate id
	 */
	Integer nextAggregateId();

	/**
	 * Create a new Aggregate instance
	 * 
	 * @return new Aggregate
	 */
	A doCreate(SessionInfo sessionInfo);

	/**
	 * Initialize Parts of new Aggregate instance
	 * 
	 * @param aggregate aggregate
	 */
	void doInitParts(A aggregate);

	/**
	 * Do some work after create, f.ex. fire events
	 * 
	 * @param aggregate aggregate
	 */
	void doAfterCreate(A aggregate);

	/**
	 * Load core aggregate data from database and instantiate a new Aggregate. This
	 * must not load Parts, they will be loaded by @see doGet below
	 * 
	 * @param id aggregate id
	 * @return instantiated Aggregate
	 */
	A doLoad(SessionInfo sessionInfo, Integer id);

	/**
	 * Load Parts from database and attach to Aggregate
	 * 
	 * @param aggregate the aggregate
	 */
	void doLoadParts(A aggregate);

	/**
	 * Do some work after load, f.ex. fire events
	 * 
	 * @param aggregate aggregate
	 */
	void doAfterLoad(A aggregate);

	/**
	 * Do some work before store, f.ex. make sure certain structures are in place
	 * 
	 * @param aggregate aggregate
	 */
	void doBeforeStore(A aggregate);

	/**
	 * Store Parts
	 * 
	 * @param aggregate aggregate
	 */
	void doStoreParts(A aggregate);

	/**
	 * Do some work after store, f.ex. fire events
	 * 
	 * @param aggregate aggregate
	 */
	void doAfterStore(A aggregate);

	/**
	 * Find aggregates (ids) according to given Query
	 * 
	 * @param querySpec query
	 */
	List<V> doFind(QuerySpec querySpec);

}
