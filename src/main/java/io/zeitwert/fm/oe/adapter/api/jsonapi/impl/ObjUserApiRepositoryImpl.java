
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjUserApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

@Controller("objUserApiRepository")
public class ObjUserApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjUserFM, ObjUserVRecord, ObjUserDto>
		implements ObjUserApiRepository {

	public ObjUserApiRepositoryImpl(ObjUserFMRepository repository, RequestContext requestCtx, ObjUserCache userCache) {
		super(ObjUserDto.class, requestCtx, userCache, repository, ObjUserDtoAdapter.getInstance());
	}

}
