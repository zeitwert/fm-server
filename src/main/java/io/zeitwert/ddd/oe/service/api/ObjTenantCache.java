package io.zeitwert.ddd.oe.service.api;

import java.util.Map;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.oe.model.ObjTenant;

public interface ObjTenantCache extends AggregateCache<ObjTenant> {

	Map<String, Integer> getStatistics();

}
