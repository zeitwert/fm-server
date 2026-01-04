package io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoBase

@JsonApiResource(type = "note", resourcePath = "collaboration/notes")
class ObjNoteDto : GenericObjDtoBase<ObjNote>()
