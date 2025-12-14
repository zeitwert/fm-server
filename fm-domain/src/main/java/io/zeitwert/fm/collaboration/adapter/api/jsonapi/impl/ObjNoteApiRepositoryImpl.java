package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.ObjNoteApiRepository;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objNoteApiRepository")
public class ObjNoteApiRepositoryImpl extends AggregateApiRepositoryBase<ObjNote, ObjNoteDto>
		implements ObjNoteApiRepository {

	public ObjNoteApiRepositoryImpl(
			ObjNoteRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userRepository,
			ObjNoteDtoAdapter dtoAdapter) {
		super(ObjNoteDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
