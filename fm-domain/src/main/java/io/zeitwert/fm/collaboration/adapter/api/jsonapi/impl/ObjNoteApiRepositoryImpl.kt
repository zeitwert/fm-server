package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import org.springframework.stereotype.Controller

@Controller("objNoteApiRepository")
open class ObjNoteApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjNoteRepository,
	adapter: ObjNoteDtoAdapter,
	sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjNote, ObjNoteDto>(
	resourceClass = ObjNoteDto::class.java,
	directory = directory,
	repository = repository,
	adapter = adapter,
	sessionCtx = sessionCtx,
)
