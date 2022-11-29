
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.adapter.api.jsonapi.ObjAccountApiRepository;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

@Controller("objAccountApiRepository")
public class ObjAccountApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjAccount, ObjAccountVRecord, ObjAccountDto>
		implements ObjAccountApiRepository {

	public ObjAccountApiRepositoryImpl(ObjAccountRepository repository, RequestContext requestCtx,
			ObjUserCache userCache) {
		super(ObjAccountDto.class, requestCtx, userCache, repository, ObjAccountDtoAdapter.getInstance());
	}

}
