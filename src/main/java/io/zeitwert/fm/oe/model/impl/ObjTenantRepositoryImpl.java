
package io.zeitwert.fm.oe.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.oe.model.base.ObjTenantRepositoryBase;
import io.zeitwert.fm.oe.model.db.Tables;
import io.zeitwert.jooq.repository.JooqAggregateFinderMixin;
import io.zeitwert.jooq.util.SqlUtils;

@Component("objTenantRepository")
public class ObjTenantRepositoryImpl extends ObjTenantRepositoryBase
		implements JooqAggregateFinderMixin<Object> {

	private final DSLContext dslContext;

	protected ObjTenantRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(appContext);
		this.dslContext = dslContext;
	}

	@Override
	public boolean hasAccountId() {
		return false;
	}

	@Override
	public DSLContext dslContext() {
		return this.dslContext;
	}

	@Override
	public final List<Object> find(QuerySpec querySpec) {
		querySpec = this.queryWithFilter(querySpec);
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return this.doFind(querySpec);
	}

	@Override
	public List<Object> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, querySpec);
	}

}
