package io.zeitwert.ddd.part.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.property.model.PropertyProvider;

/**
 * This class defines the internal callbacks for a PartRepository
 * implementation.
 */
public interface PartRepositorySPI<A extends Aggregate, P extends Part<A>> {

	P newPart(A aggregate, PartState partState);

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
