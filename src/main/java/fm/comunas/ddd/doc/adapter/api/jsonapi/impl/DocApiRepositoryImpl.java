package fm.comunas.ddd.doc.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import fm.comunas.ddd.doc.adapter.api.jsonapi.DocApiRepository;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocDto;

/**
 * Artificial crnk.io resource as parent for generic DocParts (f.ex.
 * transitions)
 */
@Controller("docApiRepository")
public class DocApiRepositoryImpl extends ResourceRepositoryBase<DocDto, Integer> implements DocApiRepository {

	@Autowired
	public DocApiRepositoryImpl() {
		super(DocDto.class);
	}

	/**
	 * We do not support standalone Doc queries.
	 */
	@Override
	public ResourceList<DocDto> findAll(QuerySpec querySpec) {
		return null;
	}

}
