package io.zeitwert.fm.task.adapter.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.app.doc.api.jsonapi.base.DocDtoBase

@JsonApiResource(type = "task", resourcePath = "collaboration/tasks")
class DocTaskDto : DocDtoBase()
