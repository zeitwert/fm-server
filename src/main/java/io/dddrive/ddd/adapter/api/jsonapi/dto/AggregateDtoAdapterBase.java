package io.dddrive.ddd.adapter.api.jsonapi.dto;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.oe.service.api.ObjTenantCache;
import io.dddrive.oe.service.api.ObjUserCache;

public abstract class AggregateDtoAdapterBase<A extends Aggregate, V extends Object, D extends AggregateDtoBase<A>> {

	private static ObjTenantCache tenantCache = null;
	private static ObjUserCache userCache = null;

	private final AppContext appContext;

	public AggregateDtoAdapterBase(AppContext appContext) {
		this.appContext = appContext;
	}

	protected AppContext getAppContext() {
		return this.appContext;
	}

	protected ObjTenant getTenant(Integer tenantId) {
		return this.getTenantCache().get(tenantId);
	}

	protected EnumeratedDto getTenantEnumerated(Integer tenantId) {
		return tenantId != null ? this.getTenantCache().getAsEnumerated(tenantId) : null;
	}

	private ObjTenantCache getTenantCache() {
		if (tenantCache == null) {
			tenantCache = (ObjTenantCache) this.appContext.getBean(ObjTenantCache.class);
		}
		return tenantCache;
	}

	protected ObjUser getUser(Integer userId) {
		return this.getUserCache().get(userId);
	}

	protected EnumeratedDto getUserEnumerated(Integer userId) {
		return userId != null ? this.getUserCache().getAsEnumerated(userId) : null;
	}

	private ObjUserCache getUserCache() {
		if (userCache == null) {
			userCache = (ObjUserCache) this.appContext.getBean(ObjUserCache.class);
		}
		return userCache;
	}

	protected EnumeratedDto asEnumerated(Aggregate a) {
		return EnumeratedDto.builder().id(a.getId().toString()).name(a.getCaption()).build();
	}

	public abstract void toAggregate(D dto, A aggregate);

	public abstract D fromAggregate(A aggregate);

	public abstract D fromRecord(V obj);

}
