package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.account.adapter.api.jsonapi.ObjAccountApiRepository;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import org.springframework.stereotype.Controller;

@Controller("objAccountApiRepository")
public class ObjAccountApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjAccount, ObjAccountDto>
		implements ObjAccountApiRepository {

	public ObjAccountApiRepositoryImpl(
			ObjAccountRepository repository,
			SessionContext requestCtx,
			ObjUserRepository userRepository,
			ObjAccountDtoAdapter dtoAdapter) {
		super(ObjAccountDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
