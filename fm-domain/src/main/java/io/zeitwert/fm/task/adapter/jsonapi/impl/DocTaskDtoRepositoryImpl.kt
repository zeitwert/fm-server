package io.zeitwert.fm.task.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.task.adapter.jsonapi.dto.DocTaskDto
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import org.springframework.stereotype.Controller

@Controller("docTaskApiRepository")
open class DocTaskDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: DocTaskRepository,
	adapter: DocTaskDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<DocTask, DocTaskDto>(
	resourceClass = DocTaskDto::class.java,
	directory = directory,
	repository = repository,
	adapter = adapter,
	sessionCtx = sessionCtx,
)
