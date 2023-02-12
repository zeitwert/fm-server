package io.dddrive.ddd.model.base;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartPersistenceProvider;
import io.dddrive.property.model.PropertyProvider;

/**
 * This class defines the internal callbacks for a PartRepository
 * implementation.
 */
public interface PartRepositorySPI<A extends Aggregate, P extends Part<A>> {

	P newPart(A aggregate, Object partState);

	/**
	 * Get the PropertyProvider for this repository
	 *
	 * @return PropertyProvider
	 */
	PropertyProvider getPropertyProvider();

	/**
	 * Get the PersistenceProvider for this repository
	 *
	 * @return PartPersistenceProvider
	 */
	PartPersistenceProvider<A, P> getPersistenceProvider();

	/**
	 * Does the part have a dedicated partId (instead of a composite key like f.ex.
	 * ObjPartItem)
	 *
	 * @return whether the part has a partId
	 */
	boolean hasPartId();

}
