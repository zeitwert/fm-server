package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.obj.model.base.ObjPersistenceProviderBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.base.ObjTenantBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantRecord;

@Configuration("tenantPersistenceProvider")
public class ObjTenantPersistenceProvider extends ObjPersistenceProviderBase<ObjTenant> {

	public ObjTenantPersistenceProvider(DSLContext dslContext) {
		super(ObjTenantRepository.class, ObjTenantBase.class, dslContext);
		this.mapField("tenantType", DbTableType.EXTN, "tenant_type_id", String.class);
		this.mapField("extlKey", DbTableType.EXTN, "extl_key", String.class);
		this.mapField("name", DbTableType.EXTN, "name", String.class);
		this.mapField("description", DbTableType.EXTN, "description", String.class);
		this.mapField("inflationRate", DbTableType.EXTN, "inflation_rate", BigDecimal.class);
		this.mapField("logoImage", DbTableType.EXTN, "logo_img_id", Integer.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjTenant.class;
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public ObjTenant doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_TENANT));
	}

	@Override
	public ObjTenant doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjTenantRecord tenantRecord = this.getDSLContext().fetchOne(Tables.OBJ_TENANT,
				Tables.OBJ_TENANT.OBJ_ID.eq(objId));
		if (tenantRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, tenantRecord);
	}

}
