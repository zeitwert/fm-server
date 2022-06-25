
package io.zeitwert.ddd.aggregate.model;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;

/**
 * A DDD Aggregate Root.
 */
public interface Aggregate {

	AggregateMeta getMeta();

	ObjTenant getTenant();

	Integer getId();

	String getCaption();

	ObjUser getOwner();

	void setOwner(ObjUser owner);

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

}
