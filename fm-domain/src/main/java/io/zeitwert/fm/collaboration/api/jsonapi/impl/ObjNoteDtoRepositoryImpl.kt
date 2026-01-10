package io.zeitwert.fm.collaboration.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.collaboration.api.jsonapi.dto.ObjNoteDto
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import org.springframework.stereotype.Controller

@Controller("objNoteApiRepository")
open class ObjNoteDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjNoteRepository,
	adapter: ObjNoteDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjNote, ObjNoteDto>(
		resourceClass = ObjNoteDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
