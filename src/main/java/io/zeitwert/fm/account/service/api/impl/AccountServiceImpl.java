package io.zeitwert.fm.account.service.api.impl;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.account.service.api.AccountService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

	private final ObjAccountRepository accountRepository;

	@Autowired
	public AccountServiceImpl(ObjAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public List<ObjAccountVRecord> getAccountList(SessionInfo sessionInfo, ObjTenant tenant) {
		return accountRepository.getByForeignKey(sessionInfo, "tenant_id", tenant.getId());
	}

}
