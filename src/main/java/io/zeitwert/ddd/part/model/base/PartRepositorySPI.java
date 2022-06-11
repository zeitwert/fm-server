package io.zeitwert.ddd.part.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import java.util.List;

/**
 * This class defines the internal callbacks for a PartRepository
 * implementation.
 */
public interface PartRepositorySPI<A extends Aggregate, P extends Part<A>> {

	/**
	 * Does the part have a dedicated partId (instead of a composite key like f.ex.
	 * ObjPartItem)
	 * 
	 * @return whether the part has a partId
	 */
	boolean hasPartId();

	/**
	 * Provide a new Part id
	 * 
	 * @return new part id
	 */
	Integer nextPartId();

	/**
	 * Create a new Part instance for the given Aggregate
	 * 
	 * @return new part
	 */
	P doCreate(A aggregate);

	/**
	 * Initialize new Part instance with basic fields after creation
	 * 
	 * @param part         part
	 * @param partId       part id
	 * @param aggregate    parent aggregate
	 * @param parent       parent part
	 * @param partListType the part list type
	 */
	void doInit(P part, Integer partId, A aggregate, Part<?> parent, CodePartListType partListType);

	/**
	 * Load all Parts from database for given aggregate
	 * 
	 * @param aggregate the aggregate
	 */
	List<P> doLoad(A aggregate);

}
