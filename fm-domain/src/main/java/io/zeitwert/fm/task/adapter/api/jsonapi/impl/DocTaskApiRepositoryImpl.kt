package io.zeitwert.fm.task.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.stereotype.Controller

@Controller("docTaskApiRepository")
open class DocTaskApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: DocTaskRepository,
	adapter: DocTaskDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateApiRepositoryBase<DocTask, DocTaskDto>(
		resourceClass = DocTaskDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
