
package io.zeitwert.ddd.aggregate.model;

import org.jooq.Record;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;

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
	 * Calculate all the derived fields, typically after a field change.
	 */
	void calcAll();

	/**
	 * Calculate all the volatile derived fields, i.e. fields that are not saved to
	 * the database. This is triggered after loading the aggregate from the
	 * database.
	 */
	void calcVolatile();

	/**
	 * Delete the Aggregate (i.e. set closed_by_user_id, closed_at)
	 */
	void delete();

}
