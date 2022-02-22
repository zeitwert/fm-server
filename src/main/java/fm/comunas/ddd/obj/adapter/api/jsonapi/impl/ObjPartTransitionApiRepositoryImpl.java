package fm.comunas.ddd.obj.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import fm.comunas.ddd.obj.adapter.api.jsonapi.ObjPartTransitionApiRepository;
import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjPartId;
import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjPartTransitionDto;

@Controller("objPartTransitionApiRepository")
public class ObjPartTransitionApiRepositoryImpl extends ResourceRepositoryBase<ObjPartTransitionDto, ObjPartId>
		implements ObjPartTransitionApiRepository {

	@Autowired
	public ObjPartTransitionApiRepositoryImpl() {
		super(ObjPartTransitionDto.class);
	}

	/**
	 * No support for standalone transition queries, must be within an Obj.
	 */
	@Override
	public ResourceList<ObjPartTransitionDto> findAll(QuerySpec querySpec) {
		return null;
	}

}
