
package io.zeitwert.fm.oe.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.oe.service.api.ObjUserCache;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.ObjTenantFMRepository;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.base.ObjTenantFMBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantRecord;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

@Component("objTenantRepository")
public class ObjTenantFMRepositoryImpl extends FMObjRepositoryBase<ObjTenantFM, ObjTenantVRecord>
		implements ObjTenantFMRepository {

	private static final String AGGREGATE_TYPE = "obj_tenant";

	private final ObjUserFMRepository userRepository;
	private final ObjAccountRepository accountRepository;
	private final ObjDocumentRepository documentRepository;
	private final ObjUserCache userCache;
	private final ObjAccountCache accountCache;

	protected ObjTenantFMRepositoryImpl(
			@Lazy ObjUserFMRepository userRepository,
			@Lazy ObjUserCache userCache,
			ObjAccountRepository accountRepository,
			ObjAccountCache accountCache,
			ObjDocumentRepository documentRepository) {
		super(ObjTenantFMRepository.class, ObjTenantFM.class, ObjTenantFMBase.class, AGGREGATE_TYPE);
		this.userRepository = userRepository;
		this.userCache = userCache;
		this.accountRepository = accountRepository;
		this.accountCache = accountCache;
		this.documentRepository = documentRepository;
	}

	@Override
	public ObjUserFMRepository getUserRepository() {
		return this.userRepository;
	}

	@Override
	public ObjUserCache getUserCache() {
		return this.userCache;
	}

	@Override
	public ObjAccountRepository getAccountRepository() {
		return this.accountRepository;
	}

	@Override
	public ObjAccountCache getAccountCache() {
		return this.accountCache;
	}

	@Override
	public ObjDocumentRepository getDocumentRepository() {
		return this.documentRepository;
	}

	public void mapProperties() {
		super.mapProperties();
		this.mapField("tenantType", AggregateState.EXTN, "tenant_type_id", String.class);
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapField("inflationRate", AggregateState.EXTN, "inflation_rate", BigDecimal.class);
		this.mapField("discountRate", AggregateState.EXTN, "discount_rate", BigDecimal.class);
		this.mapField("logoImage", AggregateState.EXTN, "logo_img_id", Integer.class);
	}

	@Override
	public boolean hasAccount() {
		return false;
	}

	@Override
	public ObjTenantFM doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_TENANT));
	}

	@Override
	public ObjTenantFM doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjTenantRecord tenantRecord = this.dslContext().fetchOne(Tables.OBJ_TENANT,
				Tables.OBJ_TENANT.OBJ_ID.eq(objId));
		if (tenantRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, tenantRecord);
	}

	@Override
	public List<ObjTenantVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

}
