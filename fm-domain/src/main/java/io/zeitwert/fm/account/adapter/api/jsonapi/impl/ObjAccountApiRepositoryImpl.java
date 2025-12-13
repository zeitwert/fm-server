
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.account.adapter.api.jsonapi.ObjAccountApiRepository;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

@Controller("objAccountApiRepository")
public class ObjAccountApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjAccount, ObjAccountDto>
		implements ObjAccountApiRepository {

	public ObjAccountApiRepositoryImpl(
			ObjAccountRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userCache,
			ObjAccountDtoAdapter dtoAdapter) {
		super(ObjAccountDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
