package io.zeitwert.fm.dms.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.dms.adapter.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.dms.model.ObjDocument
import org.springframework.stereotype.Component

@Component("objDocumentDtoAdapter")
class ObjDocumentDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjDocument, ObjDocumentDto>(
		ObjDocument::class.java,
		"document",
		ObjDocumentDto::class.java,
		directory,
		{ ObjDocumentDto() },
	) {

	init {
		config.field(
			"contentType",
			outgoing = { EnumeratedDto.of((it as ObjDocument).contentType) },
		)

		// supportedContentTypes is a calculated field from contentKind.getExtensions()
		config.field(
			"supportedContentTypes",
			outgoing = { (it as ObjDocument).contentKind?.getExtensions()?.joinToString(",") ?: "" },
		)
	}

}
