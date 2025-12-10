package io.dddrive.core.ddd.model;

import io.dddrive.core.property.model.Property;

public interface PartRepository<A extends Aggregate, P extends Part<A>> {

	/**
	 * Do we fire property change events for this property?
	 */
	boolean doLogChange(String property);

	/**
	 * Create a new Part instance within the given aggregate.
	 *
	 * @param aggregate the aggregate
	 * @param property  the (collection) property that contains the part
	 * @param partId    the part id (if loaded from persistence), or null if the id of a new part should be generated
	 */
	P create(A aggregate, Property<?> property, Integer partId);

}
