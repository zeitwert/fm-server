package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.ObjNoteApiRepository;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objNoteApiRepository")
public class ObjNoteApiRepositoryImpl extends AggregateApiRepositoryBase<ObjNote, ObjNoteDto>
		implements ObjNoteApiRepository {

	public ObjNoteApiRepositoryImpl(
			ObjNoteRepository repository,
			SessionContext sessionContext,
			ObjUserRepository userRepository,
			ObjNoteDtoAdapter dtoAdapter) {
		super(ObjNoteDto.class, sessionContext, userRepository, repository, dtoAdapter);
	}

}
