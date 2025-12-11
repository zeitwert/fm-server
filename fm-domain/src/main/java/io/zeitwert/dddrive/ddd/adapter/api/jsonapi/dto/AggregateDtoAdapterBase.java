package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto;

import org.springframework.beans.factory.annotation.Autowired;

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDtoAdapter;
import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.oe.model.ObjUser;
import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

public abstract class AggregateDtoAdapterBase<A extends Aggregate, D extends AggregateDto<A>>
		implements AggregateDtoAdapter<A, D> {

	private ObjTenantFMRepository tenantCache = null;
	private ObjUserFMRepository userCache = null;

	@Autowired
	void setTenantCache(ObjTenantFMRepository tenantCache) {
		this.tenantCache = tenantCache;
	}

	@Autowired
	void setUserCache(ObjUserFMRepository userCache) {
		this.userCache = userCache;
	}

	protected ObjTenantFMRepository getTenantCache() {
		return this.tenantCache;
	}

	protected ObjTenant getTenant(Integer tenantId) {
		return this.tenantCache.get(tenantId);
	}

	protected EnumeratedDto getTenantEnumerated(Integer tenantId) {
		return tenantId != null ? this.tenantCache.getAsEnumerated(tenantId) : null;
	}

	protected ObjUserFMRepository getUserCache() {
		return this.userCache;
	}

	protected ObjUser getUser(Integer userId) {
		return this.userCache.get(userId);
	}

	protected EnumeratedDto getUserEnumerated(Integer userId) {
		return userId != null ? this.userCache.getAsEnumerated(userId) : null;
	}

	protected EnumeratedDto asEnumerated(Aggregate a) {
		return EnumeratedDto.builder().id(a.getId().toString()).name(a.getCaption()).build();
	}

}
