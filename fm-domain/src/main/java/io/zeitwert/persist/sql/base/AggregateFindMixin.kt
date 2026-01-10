package io.zeitwert.persist.sql.base

import dddrive.ddd.query.ComparisonOperator
import dddrive.ddd.query.FilterSpec
import dddrive.ddd.query.QuerySpec
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.obj.model.db.Tables
import io.zeitwert.fm.oe.model.ObjTenantRepository
import io.zeitwert.persist.sql.util.SqlUtils
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

	/**
	 * Add tenant and account filters to the query specification.
	 * Returns a new QuerySpec with the added filters.
	 */
	fun queryWithFilter(querySpec: QuerySpec?): QuerySpec {
		val filters = mutableListOf<FilterSpec>()

		// Add existing filters
		querySpec?.filters?.let { filters.addAll(it) }

		// Add tenant filter
		val tenantId = sessionContext.tenantId as Int
		if (tenantId != ObjTenantRepository.KERNEL_TENANT_ID) { // in kernel tenant everything is visible
			filters.add(FilterSpec.Comparison(Tables.OBJ.TENANT_ID.name, ComparisonOperator.EQ, tenantId))
		}

		// Add account filter
		if (hasAccount && sessionContext.hasAccount()) {
			val accountId = sessionContext.accountId
			filters.add(FilterSpec.Comparison("account_id", ComparisonOperator.EQ, accountId))
		}

		return QuerySpec(
			filters = filters,
			sort = querySpec?.sort ?: emptyList(),
			offset = querySpec?.offset,
			limit = querySpec?.limit,
		)
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
				whereClause = sqlUtils.andFilter(whereClause, table, idField, filter)
			}
		}
		logger.trace("doFind.where(): {}", whereClause)

		// Sort.
		val sortFields = if (querySpec != null && querySpec.sort.isNotEmpty()) {
			sqlUtils.sortFields(table, querySpec.sort)
		} else if (table.field(Tables.OBJ.MODIFIED_AT.name) != null && table.field(Tables.OBJ.MODIFIED_AT.name) != null) {
			listOf(table.field(Tables.OBJ.MODIFIED_AT.name)!!.desc(), table.field(Tables.OBJ.CREATED_AT.name)!!.desc())
		} else {
			listOf(table.field(Tables.OBJ.ID.name)!!.desc())
		}

		val offset = querySpec?.offset
		val limit = querySpec?.limit

		return dslContext
			.select(idField)
			.from(table)
			.where(whereClause)
			.orderBy(sortFields)
			.limit(offset, limit)
			.fetch(idField, Any::class.java)

	}

}
