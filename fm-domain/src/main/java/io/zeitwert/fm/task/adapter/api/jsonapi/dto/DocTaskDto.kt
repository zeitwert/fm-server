package io.zeitwert.fm.task.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.DocDtoBase

@JsonApiResource(type = "task", resourcePath = "collaboration/tasks")
class DocTaskDto : DocDtoBase()
