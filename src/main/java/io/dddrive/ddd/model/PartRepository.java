
package io.dddrive.ddd.model;

import java.util.List;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.enums.CodePartListType;

public interface PartRepository<A extends Aggregate, P extends Part<A>> {

	/**
	 * Get application context
	 */
	AppContext getAppContext();

	/**
	 * Initialize an Aggregate with an empty Part list (after creation of the
	 * Aggregate)
	 *
	 * @param aggregate aggregate
	 */
	void init(A aggregate);

	/**
	 * Create a new Part instance
	 *
	 * @param aggregate    the aggregate
	 * @param partListType the part list type
	 */
	P create(A aggregate, CodePartListType partListType);

	/**
	 * Create a new Part instance
	 *
	 * @param parent       the parent part
	 * @param partListType the part list type
	 */
	P create(Part<?> parent, CodePartListType partListType);

	/**
	 * Load all the Parts for an Aggregate, regardless of whether they are directly
	 * attached to the aggregate or whether they have another part as parent. It is
	 * the aggregates responsibility to attach the parts into the corresponding
	 * collections.
	 *
	 * @param aggregate aggregate
	 */
	void load(A aggregate);

	/**
	 * Get the Parts which are directly attached to the given Aggregate.
	 *
	 * @param aggregate    the aggregate
	 * @param partListType the part list type
	 */
	List<P> getParts(A aggregate, CodePartListType partListType);

	/**
	 * Get the Parts which are directly attached to the given parent Part.
	 *
	 * @param parent       the parent part
	 * @param partListType the part list type
	 */
	List<P> getParts(Part<?> parent, CodePartListType partListType);

	/**
	 * Store the parts of the given Aggregate (create/update/delete)
	 */
	void store(A aggregate);

}
