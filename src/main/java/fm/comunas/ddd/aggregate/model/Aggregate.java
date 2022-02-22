
package fm.comunas.ddd.aggregate.model;

import fm.comunas.ddd.oe.model.ObjTenant;
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
