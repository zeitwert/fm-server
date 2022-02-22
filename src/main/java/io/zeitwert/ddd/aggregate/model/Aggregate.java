
package io.zeitwert.ddd.aggregate.model;

import io.zeitwert.ddd.oe.model.ObjTenant;
import org.jooq.Record;

/**
 * A DDD Aggregate Root.
 */
public interface Aggregate {

	AggregateRepository<? extends Aggregate, ? extends Record> getRepository();

	AggregateMeta getMeta();

	ObjTenant getTenant();

	Integer getId();

	String getCaption();

}
