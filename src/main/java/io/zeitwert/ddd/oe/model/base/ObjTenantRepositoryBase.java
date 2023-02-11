
package io.zeitwert.ddd.oe.model.base;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;

public abstract class ObjTenantRepositoryBase extends ObjRepositoryBase<ObjTenant, Object>
		implements ObjTenantRepository {

	private static final String AGGREGATE_TYPE = "obj_tenant";

	protected ObjTenantRepositoryBase(AppContext appContext) {
		super(ObjTenantRepository.class, ObjTenant.class, ObjTenantBase.class, AGGREGATE_TYPE, appContext);
	}

}
