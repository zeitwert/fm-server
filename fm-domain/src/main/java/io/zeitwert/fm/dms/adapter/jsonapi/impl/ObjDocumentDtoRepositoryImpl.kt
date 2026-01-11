package io.zeitwert.fm.dms.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.dms.adapter.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.ObjDocumentRepository
import org.springframework.stereotype.Controller

@Controller("objDocumentApiRepository")
open class ObjDocumentDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjDocumentRepository,
	adapter: ObjDocumentDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjDocument, ObjDocumentDto>(
		resourceClass = ObjDocumentDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
