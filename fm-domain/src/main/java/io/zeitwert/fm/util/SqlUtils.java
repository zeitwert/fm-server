package io.zeitwert.fm.util;

import static io.dddrive.util.Invariant.assertThis;

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

import io.crnk.core.queryspec.Direction;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.SortSpec;

public class SqlUtils {

	public interface SearchConditionProvider {
		Condition apply(Field<Integer> idField, FilterSpec filter);
	}

	private final SearchConditionProvider searchConditionProvider;

	public SqlUtils(SearchConditionProvider searchConditionProvider) {
		this.searchConditionProvider = searchConditionProvider;
	}

	public Condition andFilter(Condition whereClause, Table<?> table, Field<Integer> idField, FilterSpec filter) {
		return whereClause.and(this.filter(table, idField, filter));
	}

	public Condition orFilter(Condition whereClause, Table<?> table, Field<Integer> idField, FilterSpec filter) {
		List<Condition> conditions = filter.getExpression().stream()
				.map(f -> this.filter(table, idField, f)).toList();
		return whereClause.and(DSL.or(conditions));
	}

	public List<SortField<?>> sortFilter(Table<?> table, List<SortSpec> sortSpec) {
		return sortSpec.stream()
				.map(s -> {
					return table.field(StringUtils.toSnakeCase(s.getPath().toString()))
							.sort(Direction.ASC.equals(s.getDirection()) ? SortOrder.ASC : SortOrder.DESC).nullsLast();
				}).collect(Collectors.toList());
	}

	private Condition closedFilter(Table<?> table, FilterSpec filter) {
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

	private Integer toInteger(Object value) {
		if (value == null) {
			return null;
		} else if (value.getClass().isArray() && ((Object[]) value).length == 1) {
			return this.toInteger(((Object[]) value)[0]);
		} else if (value instanceof Collection<?> && ((Collection<?>) value).size() == 1) {
			return this.toInteger(((Collection<?>) value).stream().toList().get(0));
		} else if (value.getClass() == Integer.class) {
			return (Integer) value;
		} else if (value.getClass() == String.class) {
			return Integer.valueOf((String) value);
		}
		assertThis(false, value + " (" + value.getClass() + ") is an integer");
		return null;
	}

	@SuppressWarnings("unchecked")
	private Condition integerFilter(Field<Integer> field, FilterSpec filter) {
		if (filter.getValue() instanceof Collection) {
			if (filter.getOperator() == CustomFilters.IN || filter.getOperator() == FilterOperator.EQ) {
				return field.in((Collection<Integer>) filter.getValue());
			}
			assertThis(false, "supported integer filter operator " + filter.getOperator() + " on " + filter.getValue());
		} else {
			Integer value = this.toInteger(filter.getValue());
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

	private Condition stringFilter(Field<String> field, FilterSpec filter) {
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

	private Condition booleanFilter(Field<Boolean> field, FilterSpec filter) {
		Boolean value = filter.getValue();
		if (filter.getOperator() == FilterOperator.EQ) {
			return field.eq(value);
		}
		assertThis(false, "supported boolean filter operator " + filter.getOperator());
		return DSL.falseCondition();
	}

	private Condition localDateTimeFilter(Field<LocalDateTime> field, FilterSpec filter) {
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

	private Condition offsetDateTimeFilter(Field<OffsetDateTime> field, FilterSpec filter) {
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
	private Condition filter(Table<?> table, Field<Integer> idField, FilterSpec filter) {
		String fieldName = StringUtils.toSnakeCase(CrnkUtils.getPath(filter));
		if ("search_text".equals(fieldName)) {
			return this.searchConditionProvider.apply(idField, filter);
		} else if ("is_closed".equals(fieldName)) {
			return this.closedFilter(table, filter);
		}
		Field<?> field = table.field(fieldName);
		assertThis(field != null, "known field " + fieldName);
		if (field == null) {
			return null; // make compiler happy (potential null pointer)
		}
		if (field.getType() == Integer.class) {
			return this.integerFilter((Field<Integer>) field, filter);
		} else if (field.getType() == String.class) {
			return this.stringFilter((Field<String>) field, filter);
		} else if (field.getType() == Boolean.class) {
			return this.booleanFilter((Field<Boolean>) field, filter);
		} else if (field.getType() == LocalDateTime.class) {
			return this.localDateTimeFilter((Field<LocalDateTime>) field, filter);
		} else if (field.getType() == OffsetDateTime.class) {
			return this.offsetDateTimeFilter((Field<OffsetDateTime>) field, filter);
		}
		assertThis(false, "supported field type " + fieldName + ": " + field.getType());
		return DSL.falseCondition();
	}

}
