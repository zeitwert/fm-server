package io.zeitwert.fm.dms.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.dddrive.obj.api.jsonapi.base.ObjDtoBase

@JsonApiResource(type = "document", resourcePath = "document/documents")
class ObjDocumentDto : ObjDtoBase()
