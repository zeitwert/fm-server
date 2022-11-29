
package io.zeitwert.ddd.aggregate.service.api;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;

/**
 * An Aggregate Cache
 */
public interface AggregateCache<A extends Aggregate> {

	/**
	 * Lookup (and cache) an Aggregate with given id
	 * return aggregate
	 * throws NoDataFound exception when aggregate not found
	 */
	A get(Integer id);

	/**
	 * Lookup (and cache) an Aggregate with given id
	 * return aggregate as enumerated
	 * throws NoDataFound exception when aggregate not found
	 */
	EnumeratedDto getAsEnumerated(Integer id);

}
