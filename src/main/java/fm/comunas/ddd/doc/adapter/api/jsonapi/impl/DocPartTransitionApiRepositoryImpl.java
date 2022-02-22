package fm.comunas.ddd.doc.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import fm.comunas.ddd.doc.adapter.api.jsonapi.DocPartTransitionApiRepository;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocPartId;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocPartTransitionDto;

@Controller("docPartTransitionApiRepository")
public class DocPartTransitionApiRepositoryImpl extends ResourceRepositoryBase<DocPartTransitionDto, DocPartId>
		implements DocPartTransitionApiRepository {

	@Autowired
	public DocPartTransitionApiRepositoryImpl() {
		super(DocPartTransitionDto.class);
	}

	@Override
	public ResourceList<DocPartTransitionDto> findAll(QuerySpec querySpec) {
		return null;
	}

}
