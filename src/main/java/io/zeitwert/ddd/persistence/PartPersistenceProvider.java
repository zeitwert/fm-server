package io.zeitwert.ddd.persistence;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.base.PartStatus;
import io.zeitwert.ddd.part.model.enums.CodePartListType;

public interface PartPersistenceProvider<A extends Aggregate, P extends Part<A>> extends PropertyProvider {

	/**
	 * Provide a new Part id
	 * 
	 * @return new part id
	 */
	Integer nextPartId();

	/**
	 * Get the parts persistence status.
	 * 
	 * @return persistence status
	 */
	PartStatus getStatus(Part<?> part);

	/**
	 * Create a new Part instance for the given Aggregate
	 * 
	 * @return new part
	 */
	P doCreate(A aggregate);

	/**
	 * Load all Parts from database for given aggregate
	 * 
	 * @param aggregate the aggregate
	 */
	List<P> doLoad(A aggregate);

	/**
	 * Initialise the database records of a Part with some basic fields after
	 * creation (internal, technical callback).
	 * 
	 * @param part         part
	 * @param partId       part id
	 * @param aggregate    aggregate
	 * @param parent       parent part
	 * @param partListType part list type
	 */
	void doInit(Part<?> part, Integer partId, A aggregate, Part<?> parent, CodePartListType partListType);

	/**
	 * Store the part (insert/update/delete)
	 */
	void doStore(P part);

}
