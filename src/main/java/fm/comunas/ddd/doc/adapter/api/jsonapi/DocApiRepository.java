package fm.comunas.ddd.doc.adapter.api.jsonapi;

import io.crnk.core.repository.ResourceRepository;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocDto;

/**
 * Artificial crnk.io resource as parent for generic DocParts (f.ex.
 * transitions)
 */
public interface DocApiRepository extends ResourceRepository<DocDto, Integer> {

}
