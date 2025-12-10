package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

// TODO-MIGRATION: Phase 3 - restore adapter layer after REST API migration
// This file is commented out during OE domain migration (Phase 2b)

/*
import org.springframework.stereotype.Controller;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjUserApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserVRecord;

@Controller("objUserApiRepository")
public class ObjUserApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjUserFM, ObjUserVRecord, ObjUserDto>
		implements ObjUserApiRepository {

	public ObjUserApiRepositoryImpl(
			ObjUserFMRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjUserDtoAdapter dtoAdapter) {
		super(ObjUserDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
*/
