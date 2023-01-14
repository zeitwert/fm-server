
package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.task.adapter.api.jsonapi.DocTaskApiRepository;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

@Controller("docTaskApiRepository")
public class DocTaskApiRepositoryImpl
		extends AggregateApiRepositoryBase<DocTask, DocTaskVRecord, DocTaskDto>
		implements DocTaskApiRepository {

	public DocTaskApiRepositoryImpl(DocTaskRepository repository, RequestContext requestCtx, ObjUserCache userCache) {
		super(DocTaskDto.class, requestCtx, userCache, repository, DocTaskDtoAdapter.getInstance());
	}

}
