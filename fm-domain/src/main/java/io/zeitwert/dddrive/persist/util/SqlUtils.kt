package io.zeitwert.dddrive.persist.util

import io.crnk.core.queryspec.Direction
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.FilterSpec
import io.crnk.core.queryspec.SortSpec
import io.zeitwert.dddrive.persist.util.CrnkUtils.getPath
import io.zeitwert.fm.obj.model.db.Tables
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

	fun andFilter(
		whereClause: Condition,
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec,
	) = whereClause.and(filter(table, idField, filter))

	fun orFilter(
		whereClause: Condition,
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec,
	) = whereClause.and(DSL.or(filter.expression.map { filter(table, idField, it) }))

	fun sortFields(
		table: Table<*>,
		sortSpec: List<SortSpec>,
	) = sortSpec.map {
		table
			.field(StringUtils.toSnakeCase(it.path.toString()))!!
			.sort(if (Direction.ASC == it.direction) SortOrder.ASC else SortOrder.DESC)
			.nullsLast()
	}

	private fun closedFilter(
		table: Table<*>,
		filter: FilterSpec,
	): Condition {
		val field = table.field(Tables.OBJ.CLOSED_AT.name)!!
		val value = filter.getValue<Boolean>()
		if (value) {
			if (filter.operator === FilterOperator.EQ) {
				return field.isNotNull()
			} else if (filter.operator === FilterOperator.NEQ) {
				return field.isNull()
			}
		} else if (!value) {
			if (filter.operator === FilterOperator.EQ) {
				return field.isNull()
			} else if (filter.operator === FilterOperator.NEQ) {
				return field.isNotNull()
			}
		}
		return DSL.trueCondition()
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
			throw IllegalArgumentException("" + value + " (" + value.javaClass + ") is not an integer")
		}

	@Suppress("UNCHECKED_CAST")
	private fun integerFilter(
		field: Field<Int>,
		filter: FilterSpec,
	): Condition {
		val fieldName = StringUtils.toSnakeCase(getPath(filter))
		val fieldValue = filter.getValue<Any?>()
		logger.trace("integerFilter({} {} {} ({})", fieldName, filter.operator, fieldName, fieldValue?.javaClass)
		try {
			return if (fieldValue is Collection<*>) {
				if (filter.operator === CustomFilters.IN || filter.operator === FilterOperator.EQ) {
					return field.`in`(fieldValue as Collection<Int>?)
				} else {
					throw IllegalArgumentException("Unsupported integer collection operator $fieldName ${filter.operator} $fieldValue (${fieldValue.javaClass})")
				}
			} else {
				val intValue = toInteger(fieldValue)
				if (filter.operator === FilterOperator.EQ) {
					if (intValue != null) {
						field.eq(intValue)
					} else {
						field.isNull()
					}
				} else if (filter.operator === FilterOperator.NEQ) {
					if (intValue != null) {
						field.eq(intValue).not()
					} else {
						field.isNotNull()
					}
				} else if (filter.operator === FilterOperator.GT) {
					field.gt(intValue)
				} else if (filter.operator === FilterOperator.GE) {
					field.ge(intValue)
				} else if (filter.operator === FilterOperator.LT) {
					field.lt(intValue)
				} else if (filter.operator === FilterOperator.LE) {
					field.le(intValue)
				} else {
					throw IllegalArgumentException("Unsupported integer operator $fieldName ${filter.operator} $intValue (${intValue?.javaClass})")
				}
			}
		} catch (e: Exception) {
			throw RuntimeException(
				"integerFilter $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass}) crashed: ${e.message}",
				e,
			)
		}
	}

	private fun stringFilter(
		field: Field<String>,
		filter: FilterSpec,
	): Condition {
		if (filter.operator === CustomFilters.IN) {
			val value = filter.getValue<Set<String>>()
			var inner = DSL.noCondition()
			for (`val` in value) {
				inner = inner.or(field.eq(`val`))
			}
			return inner
		}
		val fieldName = StringUtils.toSnakeCase(getPath(filter))
		val fieldValue = filter.getValue<Any?>()?.toString()
		try {
			return if (filter.operator === FilterOperator.EQ) {
				if (fieldValue != null) {
					field.eq(fieldValue)
				} else {
					field.isNull().or(field.eq(""))
				}
			} else if (filter.operator === FilterOperator.NEQ) {
				if (fieldValue != null) {
					field.eq(fieldValue).not()
				} else {
					field.isNotNull().and(field.eq("").not())
				}
			} else if (filter.operator === FilterOperator.GT) {
				field.gt(fieldValue)
			} else if (filter.operator === FilterOperator.GE) {
				field.ge(fieldValue)
			} else if (filter.operator === FilterOperator.LT) {
				field.lt(fieldValue)
			} else if (filter.operator === FilterOperator.LE) {
				field.le(fieldValue)
			} else if (filter.operator === FilterOperator.LIKE && fieldValue != null) {
				DSL.lower(field).like(fieldValue.replace("*", "%"))
			} else {
				throw IllegalArgumentException("Unsupported string operator $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass})")
			}
		} catch (e: Exception) {
			throw RuntimeException(
				"stringFilter $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass}) crashed: ${e.message}",
				e,
			)
		}
	}

	private fun booleanFilter(
		field: Field<Boolean>,
		filter: FilterSpec,
	): Condition {
		val fieldName = StringUtils.toSnakeCase(getPath(filter))
		val anyValue = filter.getValue<Any?>()
		val fieldValue = when (anyValue) {
			is Boolean -> anyValue
			is String -> anyValue.toBoolean()
			else -> throw IllegalArgumentException("Unsupported boolean value type for $fieldName: $anyValue (${anyValue?.javaClass})")
		}
		try {
			return if (filter.operator === FilterOperator.EQ) {
				field.eq(fieldValue)
			} else {
				throw IllegalArgumentException("Unsupported boolean operator $fieldName ${filter.operator} $fieldValue (${fieldValue.javaClass})")
			}
		} catch (e: Exception) {
			throw RuntimeException(
				"booleanFilter $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass}) crashed: ${e.message}",
				e,
			)
		}
	}

	private fun localDateTimeFilter(
		field: Field<LocalDateTime>,
		filter: FilterSpec,
	): Condition {
		val fieldName = StringUtils.toSnakeCase(getPath(filter))
		val fieldValue = filter.getValue<LocalDateTime?>()
		try {
			return if (filter.operator === FilterOperator.EQ) {
				if (fieldValue != null) {
					field.eq(fieldValue)
				} else {
					field.isNull()
				}
			} else if (filter.operator === FilterOperator.NEQ) {
				if (fieldValue != null) {
					field.eq(fieldValue).not()
				} else {
					field.isNotNull()
				}
			} else if (filter.operator === FilterOperator.GT) {
				field.gt(fieldValue)
			} else if (filter.operator === FilterOperator.GE) {
				field.ge(fieldValue)
			} else if (filter.operator === FilterOperator.LT) {
				field.lt(fieldValue)
			} else if (filter.operator === FilterOperator.LE) {
				field.le(fieldValue)
			} else {
				throw IllegalArgumentException("Unsupported LocalDateTime operator $fieldName ${filter.operator} $fieldValue (${fieldValue.javaClass})")
			}
		} catch (e: Exception) {
			throw RuntimeException(
				"localDateTimeFilter $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass}) crashed: ${e.message}",
				e,
			)
		}
	}

	private fun offsetDateTimeFilter(
		field: Field<OffsetDateTime>,
		filter: FilterSpec,
	): Condition {
		val fieldName = StringUtils.toSnakeCase(getPath(filter))
		var fieldValue = filter.getValue<OffsetDateTime?>()
		try {
			return if (fieldValue == null) {
				if (filter.operator === FilterOperator.EQ) {
					field.isNull()
				} else if (filter.operator === FilterOperator.NEQ) {
					field.isNotNull()
				} else {
					throw IllegalArgumentException("Unsupported OffsetDateTime null operator $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass})")
				}
			} else {
				val zoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())
				fieldValue = fieldValue.atZoneSameInstant(zoneOffset).toOffsetDateTime()
				if (filter.operator === FilterOperator.EQ) {
					field.eq(fieldValue)
				} else if (filter.operator === FilterOperator.NEQ) {
					field.eq(fieldValue).not()
				} else if (filter.operator === FilterOperator.GT) {
					field.gt(fieldValue)
				} else if (filter.operator === FilterOperator.GE) {
					field.ge(fieldValue)
				} else if (filter.operator === FilterOperator.LT) {
					field.lt(fieldValue)
				} else if (filter.operator === FilterOperator.LE) {
					field.le(fieldValue)
				} else {
					throw IllegalArgumentException("Unsupported OffsetDateTime operator $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass})")
				}
			}
		} catch (e: Exception) {
			throw RuntimeException(
				"offsetDateTimeFilter $fieldName ${filter.operator} $fieldValue (${fieldValue?.javaClass}) crashed: ${e.message}",
				e,
			)
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun filter(
		table: Table<*>,
		idField: Field<Int>,
		filter: FilterSpec,
	): Condition? {
		try {
			val fieldName = StringUtils.toSnakeCase(getPath(filter))
			if ("is_closed" == fieldName) {
				return closedFilter(table, filter)
			} else if ("search_text" == fieldName) {
				return searchConditionProvider!!.apply(idField, filter)
			}
			val field = table.field(fieldName)!!
			return if (field.getType() == Integer::class.java) {
				integerFilter(field as Field<Int>, filter)
			} else if (field.getType() == java.lang.String::class.java) {
				stringFilter(field as Field<String>, filter)
			} else if (field.getType() == java.lang.Boolean::class.java) {
				booleanFilter(field as Field<Boolean>, filter)
			} else if (field.getType() == LocalDateTime::class.java) {
				localDateTimeFilter(field as Field<LocalDateTime>, filter)
			} else if (field.getType() == OffsetDateTime::class.java) {
				offsetDateTimeFilter(field as Field<OffsetDateTime>, filter)
			} else {
				throw IllegalArgumentException("Unsupported field type " + fieldName + ": " + field.getType())
			}
		} catch (e: Exception) {
			throw RuntimeException("filter($table.$idField, $filter) crashed ${e.message}", e)
		}
	}

	interface SearchConditionProvider {

		fun apply(
			idField: Field<Int>,
			filter: FilterSpec,
		): Condition?

	}

}
