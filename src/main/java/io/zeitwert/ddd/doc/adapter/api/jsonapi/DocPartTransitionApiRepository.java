package io.zeitwert.ddd.doc.adapter.api.jsonapi;

import io.crnk.core.repository.ResourceRepository;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocPartId;
import io.zeitwert.ddd.doc.adapter.api.jsonapi.dto.DocPartTransitionDto;

public interface DocPartTransitionApiRepository extends ResourceRepository<DocPartTransitionDto, DocPartId> {

}
