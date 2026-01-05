package io.zeitwert.fm.dms.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objDocumentDtoAdapter")
class ObjDocumentDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjDocument, ObjDocumentDto>(directory, { ObjDocumentDto() }) {

	init {
		field(
			"contentType",
			outgoing = { EnumeratedDto.of((it as ObjDocument).contentType) },
		)

		// supportedContentTypes is a calculated field from contentKind.getExtensions()
		field(
			"supportedContentTypes",
			outgoing = { (it as ObjDocument).contentKind?.getExtensions()?.joinToString(",") ?: "" },
		)
	}

}
