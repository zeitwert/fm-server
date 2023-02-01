
package io.zeitwert.fm.oe.model.impl;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.TableRecord;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.base.ObjTenantRepositoryBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantVRecord;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjTenantRepositoryBase {

	protected ObjTenantRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(appContext, dslContext);
	}

	@Override
	public ObjTenant doCreate() {
		assertThis(false, "nope");
		return null;
	}

	@Override
	public ObjTenant doLoad(Integer id) {
		assertThis(false, "nope");
		return null;
	}

	@Override
	public List<TableRecord<?>> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

	@Override
	public Optional<ObjTenant> getByExtlKey(String extlKey) {
		ObjTenantVRecord tenantRecord = this.getDSLContext().fetchOne(Tables.OBJ_TENANT_V,
				Tables.OBJ_TENANT_V.EXTL_KEY.eq(extlKey));
		if (tenantRecord == null) {
			return Optional.empty();
		}
		return Optional.of(this.get(tenantRecord.getId()));
	}

}
