package io.zeitwert.fm.dms.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "document", resourcePath = "document/documents")
class ObjDocumentDto : ObjDtoBase<ObjDocument>()
