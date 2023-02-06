package io.zeitwert.ddd.oe.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;

@Service("tenantCache")
public class ObjTenantCacheImpl extends AggregateCacheBase<ObjTenant> implements ObjTenantCache {

	public ObjTenantCacheImpl(ObjTenantRepository tenantRepository) {
		super(tenantRepository, ObjTenant.class);
	}

}
