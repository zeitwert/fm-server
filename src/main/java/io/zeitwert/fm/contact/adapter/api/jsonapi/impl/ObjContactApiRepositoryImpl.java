
package io.zeitwert.fm.contact.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.contact.adapter.api.jsonapi.ObjContactApiRepository;
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;

@Controller("objContactApiRepository")
public class ObjContactApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjContact, ObjContactVRecord, ObjContactDto>
		implements ObjContactApiRepository {

	public ObjContactApiRepositoryImpl(ObjContactRepository repository, RequestContext requestCtx,
			ObjUserCache userCache) {
		super(ObjContactDto.class, requestCtx, userCache, repository, ObjContactDtoAdapter.getInstance());
	}

}
