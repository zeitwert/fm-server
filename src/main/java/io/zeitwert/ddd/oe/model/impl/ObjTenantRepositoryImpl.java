
package io.zeitwert.ddd.oe.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.zeitwert.ddd.session.service.api.SessionService;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjRepositoryBase<ObjTenant, ObjTenantVRecord>
		implements ObjTenantRepository {

	private static final String ITEM_TYPE = "obj_tenant";

	private final SessionInfo globalSessionInfo;

	@Autowired
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
			ITEM_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository
		);
		this.globalSessionInfo = sessionService.getGlobalSession();
	}
	//@formatter:on

	@Override
	public ObjTenant doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		ObjRecord objRecord = this.dslContext.fetchOne(io.zeitwert.ddd.obj.model.db.Tables.OBJ,
				io.zeitwert.ddd.obj.model.db.Tables.OBJ.ID.eq(objId));
		ObjTenantRecord tenantRecord = this.dslContext.fetchOne(Tables.OBJ_TENANT, Tables.OBJ_TENANT.OBJ_ID.eq(objId));
		if (objRecord == null || tenantRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return newAggregate(sessionInfo, objRecord, tenantRecord);
	}

	@Override
	public List<ObjTenantVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

	@Override
	public ObjTenant get(Integer id) {
		return this.get(this.globalSessionInfo, id);
	}

	public Optional<ObjTenant> getByExtlKey(String extlKey) {
		ObjTenantVRecord tenantRecord = this.dslContext.fetchOne(Tables.OBJ_TENANT_V,
				Tables.OBJ_TENANT_V.EXTL_KEY.eq(extlKey));
		if (tenantRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(this.globalSessionInfo, tenantRecord.getId()));
	}

	@Override
	public ObjTenant doCreate(SessionInfo sessionInfo) {
		throw new RuntimeException("cannot create a Tenant");
	}

}
