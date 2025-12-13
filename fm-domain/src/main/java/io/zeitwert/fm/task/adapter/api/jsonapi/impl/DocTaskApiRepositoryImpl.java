package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.task.adapter.api.jsonapi.DocTaskApiRepository;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;

@Controller("docTaskApiRepository")
public class DocTaskApiRepositoryImpl
		extends AggregateApiRepositoryBase<DocTask, DocTaskDto>
		implements DocTaskApiRepository {

	public DocTaskApiRepositoryImpl(
			DocTaskRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userCache,
			DocTaskDtoAdapter dtoAdapter) {
		super(DocTaskDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
