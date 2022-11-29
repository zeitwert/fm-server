package io.zeitwert.ddd.oe.service.api.impl;

import java.util.Optional;

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

	public Optional<ObjTenant> getByExtlKey(String extlKey) {
		Optional<ObjTenant> maybeTenant = ((ObjTenantRepository) this.getRepository()).getByExtlKey(extlKey);
		if (maybeTenant.isPresent()) {
			ObjTenant tenant = maybeTenant.get();
			return Optional.of(this.get(tenant.getId()));
		}
		return maybeTenant;
	}

}
