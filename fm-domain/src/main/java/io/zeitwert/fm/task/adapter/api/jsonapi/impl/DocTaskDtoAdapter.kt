package io.zeitwert.fm.task.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.DocDtoAdapterBase
import io.zeitwert.fm.task.adapter.api.jsonapi.dto.DocTaskDto
import io.zeitwert.fm.task.model.DocTask
import org.springframework.stereotype.Component

@Component("docTaskDtoAdapter")
class DocTaskDtoAdapter(
	directory: RepositoryDirectory,
) : DocDtoAdapterBase<DocTask, DocTaskDto>(directory, { DocTaskDto() }) {

	init {
		config.field("relatedTo")
	}

}
