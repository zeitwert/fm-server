
package io.zeitwert.fm.search.service.api.impl;

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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.search.service.api.SearchService;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.search.model.db.Tables;

@Service("searchService")
@DependsOn("appContext")
public class SearchServiceImpl implements SearchService {

	private static final String SEARCH_TABLE_NAME = "item_search";
	private static final Table<?> SEARCH_TABLE = AppContext.getInstance().getTable(SEARCH_TABLE_NAME);
	private static final Field<String> SEARCH__ITEM_TYPE_ID = SEARCH_TABLE.field("item_type_id", String.class);
	private static final Field<Integer> SEARCH__ITEM_ID = SEARCH_TABLE.field("item_id", Integer.class);

	private static final String OBJ_TABLE_NAME = "obj";
	private static final Table<?> OBJ_TABLE = AppContext.getInstance().getTable(OBJ_TABLE_NAME);
	private static final Field<Integer> OBJ__ID = OBJ_TABLE.field("id", Integer.class);
	private static final Field<String> OBJ__CAPTION = OBJ_TABLE.field("caption", String.class);
	private static final Field<Integer> OBJ__TENANT_ID = OBJ_TABLE.field("tenant_id", Integer.class);
	private static final Field<Integer> OBJ__ACCOUNT_ID = OBJ_TABLE.field("account_id", Integer.class);

	private final CodeAggregateTypeEnum aggregateTypeEnum;
	private final DSLContext dslContext;
	private final RequestContext requestContext;

	SearchServiceImpl(CodeAggregateTypeEnum aggregateTypeEnum, DSLContext dslContext, RequestContext requestContext) {
		this.aggregateTypeEnum = aggregateTypeEnum;
		this.dslContext = dslContext;
		this.requestContext = requestContext;
	}

	@Override
	public void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens) {
		String allTexts = String.join(" ", texts.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTokens = String.join(" ", tokens.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTextsAndTokens = (allTexts + " " + allTokens).trim();
		String aggregateType = aggregate.getMeta().getAggregateType().getId();
		Integer aggregateId = aggregate.getId();
		String id = aggregateType + ":" + aggregateId;
		this.dslContext
				.delete(Tables.ITEM_SEARCH)
				.where(Tables.ITEM_SEARCH.ID.eq(id))
				.execute();
		this.dslContext
				.insertInto(
						Tables.ITEM_SEARCH,
						Tables.ITEM_SEARCH.ID,
						Tables.ITEM_SEARCH.ITEM_TYPE_ID,
						Tables.ITEM_SEARCH.ITEM_ID,
						Tables.ITEM_SEARCH.A_SIMPLE,
						Tables.ITEM_SEARCH.B_GERMAN,
						Tables.ITEM_SEARCH.B_ENGLISH)
				.values(
						id,
						aggregateType,
						aggregateId,
						allTokens,
						allTextsAndTokens,
						allTextsAndTokens)
				.execute();
	}

	public List<SearchResult> find(String searchText, int maxResultSize) {
		return this.find(List.of(), searchText, maxResultSize);
	}

	public List<SearchResult> find(List<String> itemTypes, String searchText, int maxResultSize) {

		Condition tenantCondition = OBJ__TENANT_ID.eq(this.requestContext.getTenantId());
		Condition accountCondition = OBJ__ACCOUNT_ID.isNull().or(OBJ__ACCOUNT_ID.eq(this.requestContext.getAccountId()));
		Condition itemTypeCondition = itemTypes != null && itemTypes.size() > 0
				? SEARCH__ITEM_TYPE_ID.in(itemTypes)
				: DSL.noCondition();
		String searchToken = "'" + searchText + "':*";
		Condition searchCondition = DSL.noCondition()
				.or("search_key @@ to_tsquery('simple', ?)", searchToken)
				.or("search_key @@ to_tsquery('german', ?)", searchToken)
				.or("search_key @@ to_tsquery('english', ?)", searchToken);

		//@formatter:off
		SelectWithTiesAfterOffsetStep<Record4<String, Integer, String, BigDecimal>> searchSelect = this.dslContext
			.select(
				SEARCH__ITEM_TYPE_ID,
				OBJ__ID,
				OBJ__CAPTION,
				DSL.field("(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?)))", BigDecimal.class, searchToken, searchToken, searchToken)
			)
			.from(SEARCH_TABLE)
			.join(OBJ_TABLE)
			.on(OBJ__ID.eq(SEARCH__ITEM_ID))
			.where(DSL.and(searchCondition, tenantCondition, accountCondition, itemTypeCondition))
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
