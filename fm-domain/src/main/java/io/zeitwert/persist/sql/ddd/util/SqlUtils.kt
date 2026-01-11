package io.zeitwert.persist.sql.ddd.util

import dddrive.query.ComparisonOperator
import dddrive.query.FilterSpec
import dddrive.query.SortDirection
import dddrive.query.SortSpec
import io.zeitwert.app.obj.model.db.Tables
import org.jooq.Condition
import org.jooq.Field
import org.jooq.SortOrder
import org.jooq.Table
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

class SqlUtils {

	companion object {

		val logger = LoggerFactory.getLogger(SqlUtils::class.java)!!
	}

	private val searchConditionProvider: SearchConditionProvider?

	constructor() {
		this.searchConditionProvider = null
	}

	constructor(searchConditionProvider: SearchConditionProvider?) {
		this.searchConditionProvider = searchConditionProvider
	}

	/**
	 * Add a filter to the where clause with AND.
	 */
	fun andFilter(
		whereClause: Condition,
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec,
	): Condition = whereClause.and(filterToCondition(table, idField, filter))

	/**
	 * Convert sort specifications to jOOQ sort fields.
	 */
	fun sortFields(
		table: Table<*>,
		sortSpec: List<SortSpec>,
	) = sortSpec.map {
		table
			.field(StringUtils.toSnakeCase(it.path))!!
			.sort(if (SortDirection.ASC == it.direction) SortOrder.ASC else SortOrder.DESC)
			.nullsLast()
	}

	/**
	 * Convert a FilterSpec to a jOOQ Condition.
	 */
	@Suppress("UNCHECKED_CAST")
	fun filterToCondition(
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec,
	): Condition =
		when (filter) {
			is FilterSpec.Comparison -> comparisonToCondition(table, idField, filter)
			is FilterSpec.In -> inToCondition(table, filter)
			is FilterSpec.Or -> orToCondition(table, idField, filter)
		}

	private fun comparisonToCondition(
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec.Comparison,
	): Condition {
		try {
			val fieldName = StringUtils.toSnakeCase(filter.path)

			// Handle special fields
			if ("is_closed" == fieldName) {
				return closedFilter(table, filter)
			} else if ("search_text" == fieldName) {
				return searchConditionProvider!!.applySearch(idField, filter.value?.toString())
			}

			val field = table.field(fieldName)
				?: throw IllegalArgumentException("Unknown field: $fieldName in table ${table.name}")

			return when {
				field.type == Integer::class.java -> integerComparisonFilter(field as Field<Int>, filter)

				field.type == java.lang.String::class.java -> stringComparisonFilter(field as Field<String>, filter)

				field.type == java.lang.Boolean::class.java -> booleanComparisonFilter(field as Field<Boolean>, filter)

				field.type == LocalDateTime::class.java -> localDateTimeComparisonFilter(field as Field<LocalDateTime>, filter)

				field.type == OffsetDateTime::class.java -> offsetDateTimeComparisonFilter(
					field as Field<OffsetDateTime>,
					filter,
				)

				else -> throw IllegalArgumentException("Unsupported field type $fieldName: ${field.type}")
			}
		} catch (e: Exception) {
			throw RuntimeException("comparisonToCondition($table, $filter) crashed: ${e.message}", e)
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun inToCondition(
		table: Table<*>,
		filter: FilterSpec.In,
	): Condition {
		val fieldName = StringUtils.toSnakeCase(filter.path)
		val field = table.field(fieldName)
			?: throw IllegalArgumentException("Unknown field: $fieldName in table ${table.name}")

		return when {
			field.type == Integer::class.java -> {
				(field as Field<Int>).`in`(filter.values.map { toInteger(it) })
			}

			field.type == java.lang.String::class.java -> {
				// Build OR condition for string IN
				var inner = DSL.noCondition()
				for (value in filter.values) {
					inner = inner.or((field as Field<String>).eq(value.toString()))
				}
				inner
			}

			else -> {
				throw IllegalArgumentException("Unsupported IN field type $fieldName: ${field.type}")
			}
		}
	}

	private fun orToCondition(
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec.Or,
	): Condition = DSL.or(filter.filters.map { filterToCondition(table, idField, it) })

	private fun closedFilter(
		table: Table<*>,
		filter: FilterSpec.Comparison,
	): Condition {
		val field = table.field(Tables.OBJ.CLOSED_AT.name)!!
		val value = when (val v = filter.value) {
			is Boolean -> v
			is String -> v.toBoolean()
			else -> throw IllegalArgumentException("isClosed filter value must be Boolean, got: $v")
		}
		return when (filter.operator) {
			ComparisonOperator.EQ -> if (value) field.isNotNull() else field.isNull()
			ComparisonOperator.NEQ -> if (value) field.isNull() else field.isNotNull()
			else -> DSL.trueCondition()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun toInteger(value: Any?): Int? =
		if (value == null) {
			null
		} else if (value.javaClass.isArray && (value as Array<Any>).size == 1) {
			toInteger(value[0])
		} else if (value is Collection<*> && value.size == 1) {
			toInteger(value.first())
		} else if (value.javaClass == Int::class.java || value.javaClass == Integer::class.java) {
			value as Int
		} else if (value.javaClass == String::class.java || value.javaClass == java.lang.String::class.java) {
			(value as String).toInt()
		} else {
			throw IllegalArgumentException("$value (${value.javaClass}) is not an integer")
		}

	private fun integerComparisonFilter(
		field: Field<Int>,
		filter: FilterSpec.Comparison,
	): Condition {
		val intValue = toInteger(filter.value)
		return when (filter.operator) {
			ComparisonOperator.EQ -> if (intValue != null) field.eq(intValue) else field.isNull()
			ComparisonOperator.NEQ -> if (intValue != null) field.eq(intValue).not() else field.isNotNull()
			ComparisonOperator.GT -> field.gt(intValue)
			ComparisonOperator.GE -> field.ge(intValue)
			ComparisonOperator.LT -> field.lt(intValue)
			ComparisonOperator.LE -> field.le(intValue)
			ComparisonOperator.LIKE -> throw IllegalArgumentException("LIKE not supported for integer fields")
		}
	}

	private fun stringComparisonFilter(
		field: Field<String>,
		filter: FilterSpec.Comparison,
	): Condition {
		val fieldValue = filter.value?.toString()
		return when (filter.operator) {
			ComparisonOperator.EQ -> {
				if (fieldValue != null) field.eq(fieldValue) else field.isNull().or(field.eq(""))
			}

			ComparisonOperator.NEQ -> {
				if (fieldValue != null) {
					field.eq(fieldValue).not()
				} else {
					field
						.isNotNull()
						.and(field.eq("").not())
				}
			}

			ComparisonOperator.GT -> {
				field.gt(fieldValue)
			}

			ComparisonOperator.GE -> {
				field.ge(fieldValue)
			}

			ComparisonOperator.LT -> {
				field.lt(fieldValue)
			}

			ComparisonOperator.LE -> {
				field.le(fieldValue)
			}

			ComparisonOperator.LIKE -> {
				if (fieldValue != null) {
					DSL.lower(field).like(fieldValue.replace("*", "%"))
				} else {
					throw IllegalArgumentException("LIKE requires a non-null value")
				}
			}
		}
	}

	private fun booleanComparisonFilter(
		field: Field<Boolean>,
		filter: FilterSpec.Comparison,
	): Condition {
		val fieldValue = when (val v = filter.value) {
			is Boolean -> v
			is String -> v.toBoolean()
			else -> throw IllegalArgumentException("Boolean filter value must be Boolean or String, got: $v (${v?.javaClass})")
		}
		return when (filter.operator) {
			ComparisonOperator.EQ -> field.eq(fieldValue)
			ComparisonOperator.NEQ -> field.eq(fieldValue).not()
			else -> throw IllegalArgumentException("Unsupported boolean operator: ${filter.operator}")
		}
	}

	private fun localDateTimeComparisonFilter(
		field: Field<LocalDateTime>,
		filter: FilterSpec.Comparison,
	): Condition {
		val fieldValue = filter.value as? LocalDateTime
		return when (filter.operator) {
			ComparisonOperator.EQ -> if (fieldValue != null) field.eq(fieldValue) else field.isNull()
			ComparisonOperator.NEQ -> if (fieldValue != null) field.eq(fieldValue).not() else field.isNotNull()
			ComparisonOperator.GT -> field.gt(fieldValue)
			ComparisonOperator.GE -> field.ge(fieldValue)
			ComparisonOperator.LT -> field.lt(fieldValue)
			ComparisonOperator.LE -> field.le(fieldValue)
			ComparisonOperator.LIKE -> throw IllegalArgumentException("LIKE not supported for LocalDateTime fields")
		}
	}

	private fun offsetDateTimeComparisonFilter(
		field: Field<OffsetDateTime>,
		filter: FilterSpec.Comparison,
	): Condition {
		var fieldValue = filter.value as? OffsetDateTime
		if (fieldValue != null) {
			val zoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())
			fieldValue = fieldValue.atZoneSameInstant(zoneOffset).toOffsetDateTime()
		}
		return when (filter.operator) {
			ComparisonOperator.EQ -> if (fieldValue != null) field.eq(fieldValue) else field.isNull()
			ComparisonOperator.NEQ -> if (fieldValue != null) field.eq(fieldValue).not() else field.isNotNull()
			ComparisonOperator.GT -> field.gt(fieldValue)
			ComparisonOperator.GE -> field.ge(fieldValue)
			ComparisonOperator.LT -> field.lt(fieldValue)
			ComparisonOperator.LE -> field.le(fieldValue)
			ComparisonOperator.LIKE -> throw IllegalArgumentException("LIKE not supported for OffsetDateTime fields")
		}
	}

	/**
	 * Provider for search text conditions.
	 */
	interface SearchConditionProvider {

		fun applySearch(
			idField: Field<Int>,
			searchText: String?,
		): Condition

	}

}
