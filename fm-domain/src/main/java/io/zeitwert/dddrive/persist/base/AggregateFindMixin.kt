package io.zeitwert.dddrive.persist.base

import dddrive.ddd.core.model.Aggregate
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.PathSpec
import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.persist.util.SqlUtils
import io.zeitwert.fm.obj.model.db.Tables
import io.zeitwert.fm.oe.model.ObjTenantRepository
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.Table
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory

interface AggregateFindMixin {

	companion object {

		val logger = LoggerFactory.getLogger(AggregateFindMixin::class.java)!!
	}

	val dslContext: DSLContext

	val hasAccount: Boolean

	val sqlUtils: SqlUtils

	val sessionContext: SessionContext

	fun queryWithFilter(querySpec: QuerySpec?): QuerySpec {
		val querySpec = querySpec ?: QuerySpec(Aggregate::class.java)
		val tenantField = Tables.OBJ.TENANT_ID.name
		val tenantId = sessionContext.tenantId as Int
		if (tenantId != ObjTenantRepository.KERNEL_TENANT_ID) { // in kernel tenant everything is visible
			querySpec.addFilter(PathSpec.of(tenantField).filter(FilterOperator.EQ, tenantId))
		}
		if (hasAccount && sessionContext.hasAccount()) {
			val accountId = sessionContext.accountId
			querySpec.addFilter(PathSpec.of("account_id").filter(FilterOperator.EQ, accountId))
		}
		return querySpec
	}

	fun doFind(
		table: Table<out Record>,
		idField: Field<Int>,
		querySpec: QuerySpec?,
	): List<Any> {
		logger.debug("doFind({}, {}, {})", table, idField, querySpec)
		var whereClause = DSL.noCondition()
		if (querySpec != null) {
			for (filter in querySpec.filters) {
				whereClause = if (filter.operator == FilterOperator.OR && filter.expression != null) {
					sqlUtils.orFilter(whereClause, table, idField, filter)
				} else {
					sqlUtils.andFilter(whereClause, table, idField, filter)
				}
			}
		}
		logger.trace("doFind.where(): {}", whereClause)

		// Sort.
		val sortFields = if (querySpec != null && !querySpec.sort.isEmpty()) {
			sqlUtils.sortFields(table, querySpec.sort)
		} else if (table.field(Tables.OBJ.MODIFIED_AT.name) != null && table.field(Tables.OBJ.MODIFIED_AT.name) != null) {
			listOf(table.field(Tables.OBJ.MODIFIED_AT.name)!!.desc(), table.field(Tables.OBJ.CREATED_AT.name)!!.desc())
		} else {
			listOf(table.field(Tables.OBJ.ID.name)!!.desc())
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
