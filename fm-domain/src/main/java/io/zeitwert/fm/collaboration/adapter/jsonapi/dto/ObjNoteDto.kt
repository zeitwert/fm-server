package io.zeitwert.fm.collaboration.adapter.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "note", resourcePath = "collaboration/notes")
class ObjNoteDto : ObjDtoBase()
