package io.zeitwert.ddd.oe.service.api;

import java.util.Map;
import java.util.Optional;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.model.ObjTenant;

public interface ObjTenantCache {

	ObjTenant get(Integer tenantId);

	EnumeratedDto getAsEnumerated(Integer tenantId);

	Optional<ObjTenant> getByExtlKey(String extlKey);

	Map<String, Integer> getStatistics();

}
