package io.zeitwert.fm.task.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import io.zeitwert.fm.task.adapter.api.jsonapi.DocTaskApiRepository;
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import org.springframework.stereotype.Controller;

@Controller("docTaskApiRepository")
public class DocTaskApiRepositoryImpl
		extends AggregateApiRepositoryBase<DocTask, DocTaskDto>
		implements DocTaskApiRepository {

	public DocTaskApiRepositoryImpl(
			DocTaskRepository repository,
			SessionContext requestCtx,
			ObjUserRepository userRepository,
			DocTaskDtoAdapter dtoAdapter) {
		super(DocTaskDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
