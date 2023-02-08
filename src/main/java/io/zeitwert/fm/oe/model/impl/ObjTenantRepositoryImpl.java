
package io.zeitwert.fm.oe.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.base.ObjTenantRepositoryBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.jooq.repository.JooqAggregateFinderMixin;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjTenantRepositoryBase
		implements JooqAggregateFinderMixin<Object> {

	private final DSLContext dslContext;

	protected ObjTenantRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(appContext);
		this.dslContext = dslContext;
	}

	@Override
	protected boolean hasAccountId() {
		return false;
	}

	@Override
	public DSLContext dslContext() {
		return this.dslContext;
	}

	@Override
	public List<Object> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

}
