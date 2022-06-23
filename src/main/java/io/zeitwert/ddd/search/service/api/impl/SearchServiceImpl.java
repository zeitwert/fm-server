
package io.zeitwert.ddd.search.service.api.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SelectWithTiesAfterOffsetStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.db.Tables;
import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.search.service.api.SearchService;

@Service("searchService")
public class SearchServiceImpl implements SearchService {

	private static final String SEARCH_TABLE_NAME = "item_search";
	private static final Table<?> SEARCH_TABLE = AppContext.getInstance().getSchema().getTable(SEARCH_TABLE_NAME);
	private static final Field<String> ITEM_TYPE_ID = SEARCH_TABLE.field("item_type_id", String.class);
	private static final Field<Integer> ITEM_ID = SEARCH_TABLE.field("item_id", Integer.class);

	private final CodeAggregateTypeEnum aggregateTypeEnum;
	private final DSLContext dslContext;

	SearchServiceImpl(final CodeAggregateTypeEnum aggregateTypeEnum, final DSLContext dslContext) {
		this.aggregateTypeEnum = aggregateTypeEnum;
		this.dslContext = dslContext;
	}

	public List<SearchResult> find(String searchText, int maxResultSize) {
		return this.find(List.of(), searchText, maxResultSize);
	}

	public List<SearchResult> find(List<String> itemTypes, String searchText, int maxResultSize) {

		Condition itemTypeCondition = itemTypes != null ? ITEM_TYPE_ID.in(itemTypes) : DSL.noCondition();
		String searchToken = "'" + searchText + "':*";
		Condition searchCondition = DSL.noCondition()
				.or("search_key @@ to_tsquery('simple', ?)", searchToken)
				.or("search_key @@ to_tsquery('german', ?)", searchToken)
				.or("search_key @@ to_tsquery('english', ?)", searchToken);

		//@formatter:off
		SelectWithTiesAfterOffsetStep<Record4<String, Integer, String, BigDecimal>> searchSelect = this.dslContext
			.select(
				ITEM_TYPE_ID,
				Tables.OBJ.ID,
				Tables.OBJ.CAPTION,
				DSL.field("(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?)))", BigDecimal.class, searchToken, searchToken, searchToken)
			)
			.from(SEARCH_TABLE)
			.join(Tables.OBJ)
			.on(Tables.OBJ.ID.eq(ITEM_ID))
			.where(DSL.and(searchCondition, itemTypeCondition))
			.orderBy(DSL.field("(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?))) desc", BigDecimal.class, searchToken, searchToken, searchToken))
			.limit(0, maxResultSize);
		//@formatter:on

		Result<Record4<String, Integer, String, BigDecimal>> items = searchSelect.fetch();
		List<SearchResult> result = new ArrayList<>();
		for (Record4<String, Integer, String, BigDecimal> item : items) {
			CodeAggregateType aggregateType = aggregateTypeEnum.getItem(item.value1());
			result.add(new SearchResult(null, aggregateType, item.value2(), item.value3(), item.value4()));
		}
		return result;
	}

}
