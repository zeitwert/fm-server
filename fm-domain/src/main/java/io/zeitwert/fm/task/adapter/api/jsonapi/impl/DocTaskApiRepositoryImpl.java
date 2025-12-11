package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.task.adapter.api.jsonapi.DocTaskApiRepository;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

@Controller("docTaskApiRepository")
public class DocTaskApiRepositoryImpl
		extends AggregateApiRepositoryBase<DocTask, DocTaskVRecord, DocTaskDto>
		implements DocTaskApiRepository {

	public DocTaskApiRepositoryImpl(
			DocTaskRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			DocTaskDtoAdapter dtoAdapter) {
		super(DocTaskDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
