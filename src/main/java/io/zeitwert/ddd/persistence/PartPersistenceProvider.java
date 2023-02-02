package io.zeitwert.ddd.persistence;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;

public interface PartPersistenceProvider<A extends Aggregate, P extends Part<A>> extends PropertyProvider {

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

}
