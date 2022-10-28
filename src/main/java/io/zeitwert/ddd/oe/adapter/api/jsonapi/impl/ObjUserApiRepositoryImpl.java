
package io.zeitwert.ddd.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.ObjUserApiRepository;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjUserDto;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjUserVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objUserApiRepository")
public class ObjUserApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjUser, ObjUserVRecord, ObjUserDto>
		implements ObjUserApiRepository {

	public ObjUserApiRepositoryImpl(ObjUserRepository repository, SessionInfo sessionInfo) {
		super(ObjUserDto.class, sessionInfo, repository, ObjUserDtoAdapter.getInstance());
	}

}
