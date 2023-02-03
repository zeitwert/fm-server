package io.zeitwert.ddd.persistence;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortField;
import org.jooq.Table;

import io.zeitwert.ddd.aggregate.model.Aggregate;

public interface AggregatePersistenceProvider<A extends Aggregate> extends PropertyProvider {

	/**
	 * Provide a new Aggregate id
	 * 
	 * @return new aggregate id
	 */
	Integer nextAggregateId();

	/**
	 * Create a new Aggregate instance (purely technical)
	 * 
	 * @return new Aggregate
	 */
	A doCreate();

	/**
	 * Load core aggregate data from database and instantiate a new Aggregate. This
	 * must not load Parts, they will be loaded by @see AggregateSPI.doGet
	 * 
	 * @param id aggregate id
	 * @return instantiated Aggregate
	 */
	A doLoad(Integer id);

	/**
	 * Store the database record(s) (of the Aggregate only). The Parts will be
	 * stored from the repository.
	 * 
	 * @param aggregate aggregate to store
	 */
	void doStore(A aggregate);

	/**
	 * Store the search tokens.
	 * 
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens);

	/**
	 * Execute the given query and return the result.
	 * 
	 * .select()
	 * .from(table)
	 * .where(whereClause)
	 * .orderBy(sortFields)
	 * .limit(offset, limit)
	 * .fetch();
	 * 
	 */
	Result<?> doQuery(Table<? extends Record> table, Condition whereClause, List<SortField<?>> sortFields, Long offset,
			Long limit);

}
