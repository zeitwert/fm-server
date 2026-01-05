package io.zeitwert.fm.dms.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import org.springframework.stereotype.Controller

@Controller("objDocumentApiRepository")
open class ObjDocumentApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjDocumentRepository,
	adapter: ObjDocumentDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateApiRepositoryBase<ObjDocument, ObjDocumentDto>(
		resourceClass = ObjDocumentDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
