package io.zeitwert.ddd.obj.adapter.api.jsonapi;

import io.crnk.core.repository.ResourceRepository;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjPartId;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjPartTransitionDto;

public interface ObjPartTransitionApiRepository extends ResourceRepository<ObjPartTransitionDto, ObjPartId> {

}
