
package io.zeitwert.fm.account.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.adapter.api.jsonapi.ObjAccountApiRepository;
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;

@Controller("objAccountApiRepository")
public class ObjAccountApiRepositoryImpl extends AggregateApiAdapter<ObjAccount, ObjAccountVRecord, ObjAccountDto>
		implements ObjAccountApiRepository {

	@Autowired
	public ObjAccountApiRepositoryImpl(final ObjAccountRepository repository, SessionInfo sessionInfo) {
		super(ObjAccountDto.class, sessionInfo, repository, ObjAccountDtoBridge.getInstance());
	}

}
