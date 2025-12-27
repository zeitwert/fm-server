package io.zeitwert.dddrive.persist.base

import dddrive.ddd.core.model.Aggregate
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.PathSpec
import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.persist.util.SqlUtils
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.oe.model.ObjTenantRepository
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SortField
import org.jooq.Table
import org.jooq.impl.DSL

interface AggregateFindMixin {

	val dslContext: DSLContext

	val hasAccount: Boolean

	val sqlUtils: SqlUtils

	val requestCtx: RequestContextFM

	fun queryWithFilter(querySpec: QuerySpec?): QuerySpec {
		var querySpec = querySpec
		if (querySpec == null) {
			querySpec = QuerySpec(Aggregate::class.java)
		}
		// String tenantField = AggregateFields.TENANT_ID.getName();
		val tenantField = "tenant_id"
		val tenantId = requestCtx.getTenantId() as Int
		if (tenantId != ObjTenantRepository.KERNEL_TENANT_ID) { // in kernel tenant everything is visible
			querySpec.addFilter(PathSpec.of(tenantField).filter(FilterOperator.EQ, tenantId))
		}
		if (hasAccount && requestCtx.hasAccount()) {
			val accountId = requestCtx.getAccountId()
			querySpec.addFilter(PathSpec.of("account_id").filter(FilterOperator.EQ, accountId))
		}
		return querySpec
	}

	fun doFind(
		table: Table<out Record>,
		idField: Field<Int>,
		querySpec: QuerySpec?,
	): List<Any> {
		var whereClause = DSL.noCondition()

		if (querySpec != null) {
			for (filter in querySpec.filters) {
				if (filter.operator == FilterOperator.OR && filter.expression != null) {
					whereClause = sqlUtils.orFilter(whereClause, table, idField, filter)
				} else {
					whereClause = sqlUtils.andFilter(whereClause, table, idField, filter)
				}
			}
		}

		// Sort.
		val sortFields = if (querySpec != null && !querySpec.sort.isEmpty()) {
			sqlUtils.sortFilter(table, querySpec.sort)
		} else if (table.field("modified_at") != null) {
			listOf<SortField<*>>(table.field("modified_at")!!.desc())
		} else {
			listOf<SortField<*>>(table.field("id")!!.desc())
		}

		val offset = querySpec?.getOffset()
		val limit = querySpec?.getLimit()

		return dslContext
			.select(idField)
			.from(table)
			.where(whereClause)
			.orderBy(sortFields)
			.limit(offset, limit)
			.fetch(idField, Any::class.java)

	}

}
