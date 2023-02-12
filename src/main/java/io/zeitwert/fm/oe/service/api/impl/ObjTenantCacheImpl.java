package io.zeitwert.fm.oe.service.api.impl;

import org.springframework.stereotype.Service;

import io.dddrive.ddd.service.api.base.AggregateCacheBase;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.service.api.ObjTenantCache;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;

@Service("tenantCache")
public class ObjTenantCacheImpl extends AggregateCacheBase<ObjTenant> implements ObjTenantCache {

	public ObjTenantCacheImpl(ObjTenantFMRepository tenantRepository) {
		super(tenantRepository, ObjTenant.class);
	}

}
