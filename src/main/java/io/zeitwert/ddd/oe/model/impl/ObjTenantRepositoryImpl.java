
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
import io.zeitwert.ddd.obj.model.db.tables.records.ObjRecord;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.base.ObjTenantBase;
import io.zeitwert.ddd.oe.model.db.Tables;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantRecord;
import io.zeitwert.ddd.oe.model.db.tables.records.ObjTenantVRecord;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.server.session.service.api.SessionService;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjRepositoryBase<ObjTenant, ObjTenantVRecord>
		implements ObjTenantRepository {

	private static final String AGGREGATE_TYPE = "obj_tenant";

	private final SessionInfo globalSessionInfo;

	//@formatter:off
	protected ObjTenantRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final SessionService sessionService
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
		this.globalSessionInfo = sessionService.getGlobalSession();
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public ObjTenant doCreate(SessionInfo sessionInfo) {
		throw new RuntimeException("cannot create a Tenant");
	}

	@Override
	public ObjTenant doLoad(SessionInfo sessionInfo, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjRecord objRecord = this.getDSLContext().fetchOne(io.zeitwert.ddd.obj.model.db.Tables.OBJ,
				io.zeitwert.ddd.obj.model.db.Tables.OBJ.ID.eq(objId));
		ObjTenantRecord tenantRecord = this.getDSLContext().fetchOne(Tables.OBJ_TENANT, Tables.OBJ_TENANT.OBJ_ID.eq(objId));
		if (objRecord == null || tenantRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return newAggregate(sessionInfo, objRecord, tenantRecord);
	}

	@Override
	public List<ObjTenantVRecord> doFind(SessionInfo sessionInfo, QuerySpec querySpec) {
		return this.doFind(sessionInfo, Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

	@Override
	public ObjTenant get(Integer id) {
		return this.get(this.globalSessionInfo, id);
	}

	public Optional<ObjTenant> getByExtlKey(String extlKey) {
		ObjTenantVRecord tenantRecord = this.getDSLContext().fetchOne(Tables.OBJ_TENANT_V,
				Tables.OBJ_TENANT_V.EXTL_KEY.eq(extlKey));
		if (tenantRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(this.globalSessionInfo, tenantRecord.getId()));
	}

}
