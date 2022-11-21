package io.zeitwert.ddd.oe.service.api;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.model.ObjTenant;

import java.util.Map;
import java.util.Optional;

public interface TenantService {

	ObjTenant getTenant(Integer tenantId);

	Optional<ObjTenant> getByExtlKey(String extlKey);

	EnumeratedDto getTenantEnumerated(Integer tenantId);

	Map<String, Integer> getStatistics();

}
