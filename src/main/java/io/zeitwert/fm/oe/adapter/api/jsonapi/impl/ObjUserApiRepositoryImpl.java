
package io.zeitwert.fm.oe.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.oe.adapter.api.jsonapi.ObjUserApiRepository;
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto;

@Controller("objUserApiRepository")
public class ObjUserApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjUser, Object, ObjUserDto>
		implements ObjUserApiRepository {

	public ObjUserApiRepositoryImpl(ObjUserRepository repository, RequestContext requestCtx, ObjUserCache userCache) {
		super(ObjUserDto.class, requestCtx, userCache, repository, ObjUserDtoAdapter.getInstance());
	}

}
