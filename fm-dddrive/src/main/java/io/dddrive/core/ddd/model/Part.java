package io.dddrive.core.ddd.model;

import io.dddrive.core.property.model.EntityWithProperties;

/**
 * A Part is an Entity that belongs to an Aggregate (but might be attached to another part as parent).
 */
public interface Part<A extends Aggregate> extends EntityWithProperties {

	A getAggregate();

	Integer getId();

	PartMeta<A> getMeta();

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
