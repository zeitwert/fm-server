package io.zeitwert.fm.task.adapter.api.jsonapi.impl

import dddrive.app.ddd.model.Aggregate
import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.DtoUtils
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.DocDtoAdapterBase
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto
import io.zeitwert.fm.task.model.DocTask
import org.springframework.stereotype.Component

@Component("docTaskDtoAdapter")
class DocTaskDtoAdapter(
	directory: RepositoryDirectory,
) : DocDtoAdapterBase<DocTask, DocTaskDto>(directory, { DocTaskDto() }) {

	init {
		config.field(
			"relatedTo",
			outgoing = { EnumeratedDto.of((it as DocTask).relatedTo as? Aggregate) },
			incoming = { dtoValue, task ->
				val id = (dtoValue as Map<*, *>?)?.get("id") as? String
				(task as DocTask).relatedToId = DtoUtils.idFromString(id)
			},
		)
	}

}
