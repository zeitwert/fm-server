package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;

public abstract class AggregateDtoAdapterBase<A extends Aggregate, V extends TableRecord<?>, D extends AggregateDtoBase<A>> {

	private static ObjTenantCache tenantCache = null;
	private static ObjUserCache userCache = null;

	protected ObjTenant getTenant(Integer tenantId) {
		return this.getTenantCache().get(tenantId);
	}

	protected EnumeratedDto getTenantEnumerated(Integer tenantId) {
		return this.getTenantCache().getAsEnumerated(tenantId);
	}

	private ObjTenantCache getTenantCache() {
		if (tenantCache == null) {
			tenantCache = (ObjTenantCache) AppContext.getInstance().getBean(ObjTenantCache.class);
		}
		return tenantCache;
	}

	protected ObjUser getUser(Integer userId) {
		return this.getUserCache().get(userId);
	}

	protected EnumeratedDto getUserEnumerated(Integer userId) {
		return this.getUserCache().getAsEnumerated(userId);
	}

	private ObjUserCache getUserCache() {
		if (userCache == null) {
			userCache = (ObjUserCache) AppContext.getInstance().getBean(ObjUserCache.class);
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
