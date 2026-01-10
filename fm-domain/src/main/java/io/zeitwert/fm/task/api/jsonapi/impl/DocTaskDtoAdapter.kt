package io.zeitwert.fm.task.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.dddrive.api.jsonapi.dto.DtoUtils
import io.zeitwert.dddrive.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.dddrive.doc.api.jsonapi.base.DocDtoAdapterBase
import io.zeitwert.fm.task.api.jsonapi.dto.DocTaskDto
import io.zeitwert.fm.task.model.DocTask
import org.springframework.stereotype.Component

@Component("docTaskDtoAdapter")
class DocTaskDtoAdapter(
	directory: RepositoryDirectory,
) : DocDtoAdapterBase<DocTask, DocTaskDto>(
		DocTask::class.java,
		"task",
		DocTaskDto::class.java,
		directory,
		{ DocTaskDto() },
	) {

	init {
		config.field(
			"relatedTo",
			{ task -> EnumeratedDto.of((task as DocTask).relatedTo) },
			{ dtoValue, task -> (task as DocTask).relatedToId = DtoUtils.idFromString(enumId(dtoValue)) },
		)
	}

}
