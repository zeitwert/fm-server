package io.zeitwert.ddd.oe.service.api;

import java.util.Map;
import java.util.Optional;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.oe.model.ObjTenant;

public interface ObjTenantCache extends AggregateCache<ObjTenant> {

	Optional<ObjTenant> getByExtlKey(String extlKey);

	Map<String, Integer> getStatistics();

}
