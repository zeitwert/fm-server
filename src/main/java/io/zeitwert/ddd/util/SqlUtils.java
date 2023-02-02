package io.zeitwert.ddd.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.impl.DSL;

import static io.zeitwert.ddd.util.Check.assertThis;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.crnk.core.queryspec.Direction;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.queryspec.SortSpec;

public class SqlUtils {

	private static final String SEARCH_TABLE_NAME = "item_search";
	private static final Table<?> SEARCH_TABLE = AppContext.getInstance().getTable(SEARCH_TABLE_NAME);
	private static final Field<Integer> ITEM_ID = DSL.field("item_id", Integer.class);

	public static boolean hasFilterFor(QuerySpec querySpec, String fieldName) {
		return querySpec.getFilters().stream().anyMatch(f -> getPath(f).equals(fieldName));
	}

	public static Condition andFilter(Condition whereClause, Table<?> table,
			Field<Integer> idField, FilterSpec filter) {
		return whereClause.and(filter(table, idField, filter));
	}

	public static Condition orFilter(Condition whereClause, Table<?> table, Field<Integer> idField,
			FilterSpec filter) {
		List<Condition> conditions = filter.getExpression().stream()
				.map(f -> filter(table, idField, f)).toList();
		return whereClause.and(DSL.or(conditions));
	}

	public static List<SortField<?>> sortFilter(Table<?> table, List<SortSpec> sortSpec) {
		//@formatter:off
		return sortSpec.stream()
			.map(s -> {
				return table.field(StringUtils.toSnakeCase(s.getPath().toString()))
				.sort(Direction.ASC.equals(s.getDirection()) ? SortOrder.ASC : SortOrder.DESC).nullsLast();
			}).collect(Collectors.toList());
		//@formatter:on
	}

	private static String getPath(FilterSpec filter) {
		return String.join(".", filter.getPath().getElements()).replace(".id", "Id");
	}

	private static Condition searchFilter(Field<Integer> idField, FilterSpec filter) {
		String searchText = filter.getValue();
		String searchToken = "'" + searchText + "':*";
		//@formatter:off
		return idField.in(
			DSL
				.select(ITEM_ID)
				.from(SEARCH_TABLE)
				.where(
					DSL.noCondition()
						.or("search_key @@ to_tsquery('simple', ?)", searchToken)
						.or("search_key @@ to_tsquery('german', ?)", searchToken)
						.or("search_key @@ to_tsquery('english', ?)", searchToken)
				)
				.orderBy(DSL.field("(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?))) desc", BigDecimal.class, searchToken, searchToken, searchToken))
			);
		//@formatter:on
	}

	private static Condition closedFilter(Table<?> table, FilterSpec filter) {
		Field<?> field = table.field("closed_at");
		assertThis(field != null, "known field closed_at");
		if (field == null) {
			return null; // make compiler happy (potential null pointer)
		}
		Boolean value = filter.getValue();
		if (value) {
			if (filter.getOperator() == FilterOperator.EQ) {
				return field.isNotNull();
			} else if (filter.getOperator() == FilterOperator.NEQ) {
				return field.isNull();
			}
		} else if (!value) {
			if (filter.getOperator() == FilterOperator.EQ) {
				return field.isNull();
			} else if (filter.getOperator() == FilterOperator.NEQ) {
				return field.isNotNull();
			}
		}
		return DSL.trueCondition();
	}

	private static Integer toInteger(Object value) {
		if (value == null) {
			return null;
		} else if (value.getClass().isArray() && ((Object[]) value).length == 1) {
			return toInteger(((Object[]) value)[0]);
		} else if (value instanceof Collection<?> && ((Collection<?>) value).size() == 1) {
			return toInteger(((Collection<?>) value).stream().toList().get(0));
		} else if (value.getClass() == Integer.class) {
			return (Integer) value;
		} else if (value.getClass() == String.class) {
			return Integer.valueOf((String) value);
		}
		assertThis(false, value + " (" + value.getClass() + ") is an integer");
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Condition integerFilter(Field<Integer> field, FilterSpec filter) {
		if (filter.getValue() instanceof Collection) {
			if (filter.getOperator() == CustomFilters.IN || filter.getOperator() == FilterOperator.EQ) {
				return field.in((Collection<Integer>) filter.getValue());
			}
			assertThis(false, "supported integer filter operator " + filter.getOperator() + " on " + filter.getValue());
		} else {
			Integer value = toInteger(filter.getValue());
			if (filter.getOperator() == FilterOperator.EQ) {
				if (value != null) {
					return field.eq(value);
				} else {
					return field.isNull();
				}
			} else if (filter.getOperator() == FilterOperator.NEQ) {
				if (value != null) {
					return field.eq(value).not();
				} else {
					return field.isNotNull();
				}
			} else if (filter.getOperator() == FilterOperator.GT) {
				return field.gt(value);
			} else if (filter.getOperator() == FilterOperator.GE) {
				return field.ge(value);
			} else if (filter.getOperator() == FilterOperator.LT) {
				return field.lt(value);
			} else if (filter.getOperator() == FilterOperator.LE) {
				return field.le(value);
			}
		}
		assertThis(false, "supported integer filter operator " + filter.getOperator() + " on " + filter.getValue());
		return DSL.falseCondition();
	}

	private static Condition stringFilter(Field<String> field, FilterSpec filter) {
		if (filter.getOperator() == CustomFilters.IN) {
			Set<String> value = filter.getValue();
			Condition inner = DSL.noCondition();
			for (String val : value) {
				inner = inner.or(field.eq(val));
			}
			return inner;
		}

		String value = filter.getValue() != null ? filter.getValue().toString() : null;
		if (filter.getOperator() == FilterOperator.EQ) {
			if (value != null) {
				return field.eq(value);
			} else {
				return field.isNull().or(field.eq(""));
			}
		} else if (filter.getOperator() == FilterOperator.NEQ) {
			if (value != null) {
				return field.eq(value).not();
			} else {
				return field.isNotNull().and(field.eq("").not());
			}
		} else if (filter.getOperator() == FilterOperator.GT) {
			return field.gt(value);
		} else if (filter.getOperator() == FilterOperator.GE) {
			return field.ge(value);
		} else if (filter.getOperator() == FilterOperator.LT) {
			return field.lt(value);
		} else if (filter.getOperator() == FilterOperator.LE) {
			return field.le(value);
		} else if (filter.getOperator() == FilterOperator.LIKE && value != null) {
			return DSL.lower(field).like(value.replace("*", "%"));
		}

		assertThis(false, "supported string filter operator " + filter.getOperator());
		return DSL.falseCondition();
	}

	private static Condition booleanFilter(Field<Boolean> field, FilterSpec filter) {
		Boolean value = filter.getValue();
		if (filter.getOperator() == FilterOperator.EQ) {
			return field.eq(value);
		}
		assertThis(false, "supported boolean filter operator " + filter.getOperator());
		return DSL.falseCondition();
	}

	private static Condition localDateTimeFilter(Field<LocalDateTime> field, FilterSpec filter) {
		LocalDateTime value = filter.getValue();
		if (filter.getOperator() == FilterOperator.EQ) {
			if (value != null) {
				return field.eq(value);
			} else {
				return field.isNull();
			}
		} else if (filter.getOperator() == FilterOperator.NEQ) {
			if (value != null) {
				return field.eq(value).not();
			} else {
				return field.isNotNull();
			}
		} else if (filter.getOperator() == FilterOperator.GT) {
			return field.gt(value);
		} else if (filter.getOperator() == FilterOperator.GE) {
			return field.ge(value);
		} else if (filter.getOperator() == FilterOperator.LT) {
			return field.lt(value);
		} else if (filter.getOperator() == FilterOperator.LE) {
			return field.le(value);
		}
		assertThis(false, "supported local date time filter operator " + filter.getOperator());
		return DSL.falseCondition();
	}

	private static Condition offsetDateTimeFilter(Field<OffsetDateTime> field, FilterSpec filter) {
		OffsetDateTime value = (OffsetDateTime) filter.getValue();
		if (value == null) {
			if (filter.getOperator() == FilterOperator.EQ) {
				return field.isNull();
			} else if (filter.getOperator() == FilterOperator.NEQ) {
				return field.isNotNull();
			}
		} else {
			ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());
			value = value.atZoneSameInstant(zoneOffset).toOffsetDateTime();
			if (filter.getOperator() == FilterOperator.EQ) {
				return field.eq(value);
			} else if (filter.getOperator() == FilterOperator.NEQ) {
				return field.eq(value).not();
			} else if (filter.getOperator() == FilterOperator.GT) {
				return field.gt(value);
			} else if (filter.getOperator() == FilterOperator.GE) {
				return field.ge(value);
			} else if (filter.getOperator() == FilterOperator.LT) {
				return field.lt(value);
			} else if (filter.getOperator() == FilterOperator.LE) {
				return field.le(value);
			}
		}
		assertThis(false, "supported offset date time filter operator " + filter.getOperator());
		return DSL.falseCondition();
	}

	@SuppressWarnings("unchecked")
	private static Condition filter(Table<?> table, Field<Integer> idField, FilterSpec filter) {
		String fieldName = StringUtils.toSnakeCase(getPath(filter));
		if ("search_text".equals(fieldName)) {
			return searchFilter(idField, filter);
		} else if ("is_closed".equals(fieldName)) {
			return closedFilter(table, filter);
		}
		Field<?> field = table.field(fieldName);
		assertThis(field != null, "known field " + fieldName);
		if (field == null) {
			return null; // make compiler happy (potential null pointer)
		}
		if (field.getType() == Integer.class) {
			return integerFilter((Field<Integer>) field, filter);
		} else if (field.getType() == String.class) {
			return stringFilter((Field<String>) field, filter);
		} else if (field.getType() == Boolean.class) {
			return booleanFilter((Field<Boolean>) field, filter);
		} else if (field.getType() == LocalDateTime.class) {
			return localDateTimeFilter((Field<LocalDateTime>) field, filter);
		} else if (field.getType() == OffsetDateTime.class) {
			return offsetDateTimeFilter((Field<OffsetDateTime>) field, filter);
		}
		assertThis(false, "supported field type " + fieldName + ": " + field.getType());
		return DSL.falseCondition();
	}

}
