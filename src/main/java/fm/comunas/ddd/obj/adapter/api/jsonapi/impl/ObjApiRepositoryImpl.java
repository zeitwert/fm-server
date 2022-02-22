package fm.comunas.ddd.obj.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import fm.comunas.ddd.obj.adapter.api.jsonapi.ObjApiRepository;
import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjDto;

/**
 * Artificial crnk.io resource as parent for generic ObjParts (f.ex.
 * transitions)
 */
@Controller("objApiRepository")
public class ObjApiRepositoryImpl extends ResourceRepositoryBase<ObjDto, Integer> implements ObjApiRepository {

	@Autowired
	public ObjApiRepositoryImpl() {
		super(ObjDto.class);
	}

	/**
	 * We do not support standalone Obj queries.
	 */
	@Override
	public ResourceList<ObjDto> findAll(QuerySpec querySpec) {
		return null;
	}

}
