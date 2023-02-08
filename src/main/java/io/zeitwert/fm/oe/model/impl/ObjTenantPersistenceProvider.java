package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantRecord;
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.persistence.ObjExtnPersistenceProviderBase;

@Configuration("tenantPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjTenantPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjTenant> {

	public ObjTenantPersistenceProvider(DSLContext dslContext) {
		super(ObjTenant.class, dslContext);
		this.mapField("tenantType", AggregateState.EXTN, "tenant_type_id", String.class);
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapField("inflationRate", AggregateState.EXTN, "inflation_rate", BigDecimal.class);
		this.mapField("logoImage", AggregateState.EXTN, "logo_img_id", Integer.class);
	}

	@Override
	public ObjTenant doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_TENANT));
	}

	@Override
	public ObjTenant doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjTenantRecord tenantRecord = this.dslContext().fetchOne(Tables.OBJ_TENANT,
				Tables.OBJ_TENANT.OBJ_ID.eq(objId));
		if (tenantRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, tenantRecord);
	}

}
