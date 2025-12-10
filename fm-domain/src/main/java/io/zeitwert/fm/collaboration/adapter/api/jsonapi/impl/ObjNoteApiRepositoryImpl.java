package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

// TODO-MIGRATION: REST-API - remove after Phase 3 (REST API migration)
// This JSON:API repository uses OLD dddrive base classes. Will be replaced with REST controller in Phase 3.

/*
import org.springframework.stereotype.Controller;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.ObjNoteApiRepository;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;

@Controller("objNoteApiRepository")
public class ObjNoteApiRepositoryImpl extends AggregateApiRepositoryBase<ObjNote, ObjNoteVRecord, ObjNoteDto>
		implements ObjNoteApiRepository {

	public ObjNoteApiRepositoryImpl(
			ObjNoteRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjNoteDtoAdapter dtoAdapter) {
		super(ObjNoteDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
*/
