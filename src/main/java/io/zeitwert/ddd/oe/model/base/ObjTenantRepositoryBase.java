
package io.zeitwert.ddd.oe.model.base;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;

public abstract class ObjTenantRepositoryBase extends ObjRepositoryBase<ObjTenant, TableRecord<?>>
		implements ObjTenantRepository {

	private static final String AGGREGATE_TYPE = "obj_tenant";

	protected ObjTenantRepositoryBase(final AppContext appContext, final DSLContext dslContext) {
		super(
				ObjTenantRepository.class,
				ObjTenant.class,
				ObjTenantBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

}
