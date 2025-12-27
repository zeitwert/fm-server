package io.zeitwert.fm.account.service.api.impl;

import io.zeitwert.fm.account.model.db.Tables;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.service.api.AccountService;
import io.zeitwert.fm.oe.model.ObjTenant;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private final DSLContext dslContext;

	public AccountServiceImpl(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public List<ObjAccountVRecord> getAccounts(ObjTenant tenant) {
		return this.dslContext
				.selectFrom(Tables.OBJ_ACCOUNT_V)
				.where(Tables.OBJ_ACCOUNT_V.TENANT_ID.eq((Integer) tenant.getId()))
				.fetch();
	}

}
