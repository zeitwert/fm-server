
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.account.adapter.api.jsonapi.ObjAccountApiRepository;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

@Controller("objAccountApiRepository")
public class ObjAccountApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjAccount, ObjAccountVRecord, ObjAccountDto>
		implements ObjAccountApiRepository {

	public ObjAccountApiRepositoryImpl(
			ObjAccountRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjAccountDtoAdapter dtoAdapter) {
		super(ObjAccountDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
