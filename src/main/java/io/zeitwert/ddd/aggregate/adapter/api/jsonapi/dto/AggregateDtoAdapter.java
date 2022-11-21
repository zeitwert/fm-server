package io.zeitwert.ddd.aggregate.adapter.api.jsonapi.dto;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.service.api.TenantService;
import io.zeitwert.ddd.oe.service.api.UserService;

import org.jooq.TableRecord;

public abstract class AggregateDtoAdapter<A extends Aggregate, V extends TableRecord<?>, D extends AggregateDtoBase<A>> {

	private static TenantService tenantService = null;
	private static UserService userService = null;

	protected TenantService getTenantService() {
		if (tenantService == null) {
			tenantService = (TenantService) AppContext.getInstance().getBean(TenantService.class);
		}
		return tenantService;
	}

	protected UserService getUserService() {
		if (userService == null) {
			userService = (UserService) AppContext.getInstance().getBean(UserService.class);
		}
		return userService;
	}

	protected ObjTenant getTenant(Integer tenantId) {
		return this.getTenantService().getTenant(tenantId);
	}

	protected EnumeratedDto getTenantEnumerated(Integer tenantId) {
		return this.getTenantService().getTenantEnumerated(tenantId);
	}

	protected ObjUser getUser(Integer userId) {
		return this.getUserService().getUser(userId);
	}

	protected EnumeratedDto getUserEnumerated(Integer userId) {
		return this.getUserService().getUserEnumerated(userId);
	}

	public abstract void toAggregate(D dto, A aggregate);

	public abstract D fromAggregate(A aggregate);

	public abstract D fromRecord(V obj);

}
