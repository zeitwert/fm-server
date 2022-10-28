
package io.zeitwert.ddd.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.base.ObjTenantBase;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantRecord;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.session.model.RequestContext;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjRepositoryBase<ObjTenant, ObjTenantVRecord>
		implements ObjTenantRepository {

	private static final String AGGREGATE_TYPE = "obj_tenant";

	//@formatter:off
	protected ObjTenantRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository
	) {
		super(
			ObjTenantRepository.class,
			ObjTenant.class,
			ObjTenantBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository
		);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public ObjTenant doCreate(RequestContext requestCtx) {
		return this.doCreate(requestCtx, this.getDSLContext().newRecord(Tables.OBJ_TENANT));
	}

	@Override
	public ObjTenant doLoad(RequestContext requestCtx, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjTenantRecord tenantRecord = this.getDSLContext().fetchOne(Tables.OBJ_TENANT, Tables.OBJ_TENANT.OBJ_ID.eq(objId));
		if (tenantRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(requestCtx, objId, tenantRecord);
	}

	@Override
	public List<ObjTenantVRecord> doFind(RequestContext requestCtx, QuerySpec querySpec) {
		return this.doFind(requestCtx, Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

	public Optional<ObjTenant> getByExtlKey(RequestContext requestCtx, String extlKey) {
		ObjTenantVRecord tenantRecord = this.getDSLContext().fetchOne(Tables.OBJ_TENANT_V,
				Tables.OBJ_TENANT_V.EXTL_KEY.eq(extlKey));
		if (tenantRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(requestCtx, tenantRecord.getId()));
	}

}
