package fm.comunas.ddd.doc.adapter.api.jsonapi;

import io.crnk.core.repository.ResourceRepository;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocPartId;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocPartTransitionDto;

public interface DocPartTransitionApiRepository extends ResourceRepository<DocPartTransitionDto, DocPartId> {

}
