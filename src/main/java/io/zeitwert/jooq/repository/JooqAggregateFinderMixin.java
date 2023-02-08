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
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.jooq.util.SqlUtils;

public interface JooqAggregateFinderMixin<V extends Object> {

	DSLContext dslContext();

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
