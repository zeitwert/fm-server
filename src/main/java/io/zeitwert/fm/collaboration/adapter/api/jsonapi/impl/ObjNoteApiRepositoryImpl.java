
package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.ObjNoteApiRepository;
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;

@Controller("objNoteApiRepository")
public class ObjNoteApiRepositoryImpl extends AggregateApiAdapter<ObjNote, ObjNoteVRecord, ObjNoteDto>
		implements ObjNoteApiRepository {

	public ObjNoteApiRepositoryImpl(final ObjNoteRepository repository, SessionInfo sessionInfo) {
		super(ObjNoteDto.class, sessionInfo, repository, ObjNoteDtoBridge.getInstance());
	}

}
