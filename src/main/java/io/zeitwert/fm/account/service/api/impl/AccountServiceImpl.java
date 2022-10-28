package io.zeitwert.fm.account.service.api.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.service.api.AccountService;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private final ObjAccountRepository accountRepository;

	public AccountServiceImpl(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public List<ObjAccountVRecord> getAccountList(ObjTenant tenant) {
		return accountRepository.getByForeignKey("tenant_id", tenant.getId());
	}

}
