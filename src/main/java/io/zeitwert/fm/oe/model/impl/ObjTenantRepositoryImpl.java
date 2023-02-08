
package io.zeitwert.fm.oe.model.impl;

import java.util.List;

import org.jooq.TableRecord;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.base.ObjTenantRepositoryBase;
import io.zeitwert.fm.oe.model.db.Tables;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjTenantRepositoryBase {

	protected ObjTenantRepositoryImpl(AppContext appContext) {
		super(appContext);
	}

	@Override
	protected boolean hasAccountId() {
		return false;
	}

	@Override
	public List<TableRecord<?>> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

}
