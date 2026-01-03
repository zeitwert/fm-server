package io.zeitwert.fm.dms.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objDocumentDtoAdapter")
class ObjDocumentDtoAdapter(
	directory: RepositoryDirectory,
) : GenericObjDtoAdapterBase<ObjDocument, ObjDocumentDto>(directory, { ObjDocumentDto() }) {

	init {
		// contentType is a computed property (not a delegated property), so we handle it manually
		field(
			"contentType",
			outgoing = { entity, _ ->
				val document = entity as ObjDocument
				EnumeratedDto.of(document.contentType)
			},
			incoming = { _, _ ->
				// contentType is read-only (calculated from stored content)
			},
		)

		// supportedContentTypes is a calculated field from contentKind.getExtensions()
		field(
			"supportedContentTypes",
			outgoing = { entity, _ ->
				val document = entity as ObjDocument
				document.contentKind?.getExtensions()?.joinToString(",") ?: ""
			},
			incoming = { _, _ ->
				// supportedContentTypes is read-only (calculated)
			},
		)
	}

}
