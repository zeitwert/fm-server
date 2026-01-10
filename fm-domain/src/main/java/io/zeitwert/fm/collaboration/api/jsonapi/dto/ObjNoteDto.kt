package io.zeitwert.fm.collaboration.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.dddrive.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "note", resourcePath = "collaboration/notes")
class ObjNoteDto : ObjDtoBase()
