
package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.contact.adapter.api.jsonapi.ObjContactApiRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;

@Controller("objContactApiRepository")
public class ObjContactApiRepositoryImpl extends AggregateApiAdapter<ObjContact, ObjContactVRecord, ObjContactDto>
		implements ObjContactApiRepository {

	public ObjContactApiRepositoryImpl(final ObjContactRepository repository, SessionInfo sessionInfo) {
		super(ObjContactDto.class, sessionInfo, repository, ObjContactDtoBridge.getInstance());
	}

}
