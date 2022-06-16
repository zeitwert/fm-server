
package io.zeitwert.ddd.aggregate.model;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;

import org.jooq.Record;

/**
 * A DDD Aggregate Root.
 */
public interface Aggregate {

	AggregateRepository<? extends Aggregate, ? extends Record> getRepository();

	AggregateMeta getMeta();

	ObjTenant getTenant();

	ObjUser getOwner();

	void setOwner(ObjUser owner);

	Integer getId();

	String getCaption();

	/**
	 * Delete the Aggregate (i.e. set closed_by_user_id, closed_at)
	 */
	void delete();

}
