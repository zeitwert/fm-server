package io.zeitwert.jooq.repository;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.base.AggregateFields;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.jooq.util.SqlUtils;

public interface JooqAggregateFinderMixin<V extends Object> {

	DSLContext dslContext();

	boolean hasAccountId();

	List<V> doFind(QuerySpec querySpec);

	default QuerySpec queryWithFilter(QuerySpec querySpec) {
		if (querySpec == null) {
			querySpec = new QuerySpec(Aggregate.class);
		}
		RequestContext requestCtx = AppContext.getInstance().getRequestContext();
		String tenantField = AggregateFields.TENANT_ID.getName();
		Integer tenantId = requestCtx.getTenantId();
		if (tenantId != ObjTenantRepository.KERNEL_TENANT_ID) { // in kernel tenant everything is visible
			querySpec.addFilter(PathSpec.of(tenantField).filter(FilterOperator.EQ, tenantId));
		}
		if (this.hasAccountId() && requestCtx.hasAccount()) {
			String accountField = AggregateFields.ACCOUNT_ID.getName();
			Integer accountId = requestCtx.getAccountId();
			querySpec.addFilter(PathSpec.of(accountField).filter(FilterOperator.EQ, accountId));
		}
		return querySpec;
	}

	@SuppressWarnings("unchecked")
	default List<V> doFind(Table<? extends Record> table, Field<Integer> idField, QuerySpec querySpec) {

		Condition whereClause = DSL.noCondition();

		if (querySpec != null) {
			for (FilterSpec filter : querySpec.getFilters()) {
				if (filter.getOperator().equals(FilterOperator.OR) && filter.getExpression() != null) {
					whereClause = SqlUtils.orFilter(whereClause, table, idField, filter);
				} else {
					whereClause = SqlUtils.andFilter(whereClause, table, idField, filter);
				}
			}
		}

		// Sort.
		List<SortField<?>> sortFields = List.of();
		if (querySpec != null && querySpec.getSort().size() > 0) {
			sortFields = SqlUtils.sortFilter(table, querySpec.getSort());
		} else if (table.field("modified_at") != null) {
			sortFields = List.of(table.field("modified_at").desc());
		} else {
			sortFields = List.of(table.field("id").desc());
		}

		Long offset = querySpec == null ? null : querySpec.getOffset();
		Long limit = querySpec == null ? null : querySpec.getLimit();

		return (List<V>) this.doQuery(table, whereClause, sortFields, offset, limit);

	}

	default Result<?> doQuery(Table<? extends Record> table, Condition whereClause, List<SortField<?>> sortFields,
			Long offset,
			Long limit) {
		return this.dslContext()
				.select()
				.from(table)
				.where(whereClause)
				.orderBy(sortFields)
				.limit(offset, limit)
				.fetch();
	}

}
