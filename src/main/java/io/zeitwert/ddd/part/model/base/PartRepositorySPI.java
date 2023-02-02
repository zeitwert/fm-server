package io.zeitwert.ddd.part.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;

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

}
