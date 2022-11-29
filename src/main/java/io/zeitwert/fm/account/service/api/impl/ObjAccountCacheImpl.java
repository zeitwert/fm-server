package io.zeitwert.fm.account.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;

@Service("accountCache")
public class ObjAccountCacheImpl extends AggregateCacheBase<ObjAccount> implements ObjAccountCache {

	public ObjAccountCacheImpl(ObjAccountRepository repository) {
		super(repository, ObjAccount.class);
	}

}
