package fm.comunas.ddd.obj.adapter.api.jsonapi;

import io.crnk.core.repository.ResourceRepository;
import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjDto;

/**
 * Artificial crnk.io resource as parent for generic ObjParts (f.ex.
 * transitions)
 */
public interface ObjApiRepository extends ResourceRepository<ObjDto, Integer> {

}
