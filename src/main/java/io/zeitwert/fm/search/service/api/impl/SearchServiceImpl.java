
package io.zeitwert.fm.search.service.api.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SelectWithTiesAfterOffsetStep;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateType;
import io.zeitwert.ddd.aggregate.model.enums.CodeAggregateTypeEnum;
import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.search.service.api.SearchService;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.obj.model.db.tables.Obj;
import io.zeitwert.fm.search.model.db.Tables;
import io.zeitwert.fm.search.model.db.tables.ItemSearch;

@Service("searchService")
@DependsOn("appContext")
public class SearchServiceImpl implements SearchService {

	private static final ItemSearch ITEM_SEARCH = Tables.ITEM_SEARCH;
	private static final Obj OBJ = io.zeitwert.fm.obj.model.db.Tables.OBJ;

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
				.delete(ITEM_SEARCH)
				.where(ITEM_SEARCH.ID.eq(id))
				.execute();
		this.dslContext
				.insertInto(
						ITEM_SEARCH,
						ITEM_SEARCH.ID,
						ITEM_SEARCH.ITEM_TYPE_ID,
						ITEM_SEARCH.ITEM_ID,
						ITEM_SEARCH.A_SIMPLE,
						ITEM_SEARCH.B_GERMAN,
						ITEM_SEARCH.B_ENGLISH)
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

		Condition tenantCondition = OBJ.TENANT_ID.eq(this.requestContext.getTenantId());
		Condition accountCondition = OBJ.ACCOUNT_ID.isNull().or(OBJ.ACCOUNT_ID.eq(this.requestContext.getAccountId()));
		Condition itemTypeCondition = itemTypes != null && itemTypes.size() > 0
				? ITEM_SEARCH.ITEM_TYPE_ID.in(itemTypes)
				: DSL.noCondition();
		String searchToken = "'" + searchText + "':*";
		Condition searchCondition = DSL.noCondition()
				.or("search_key @@ to_tsquery('simple', ?)", searchToken)
				.or("search_key @@ to_tsquery('german', ?)", searchToken)
				.or("search_key @@ to_tsquery('english', ?)", searchToken);

		SelectWithTiesAfterOffsetStep<Record4<String, Integer, String, BigDecimal>> searchSelect = this.dslContext
				.select(
						ITEM_SEARCH.ITEM_TYPE_ID,
						OBJ.ID,
						OBJ.CAPTION,
						DSL.field(
								"(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?)))",
								BigDecimal.class, searchToken, searchToken, searchToken))
				.from(ITEM_SEARCH)
				.join(OBJ)
				.on(OBJ.ID.eq(ITEM_SEARCH.ITEM_ID))
				.where(DSL.and(searchCondition, tenantCondition, accountCondition, itemTypeCondition))
				.orderBy(DSL.field(
						"(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?))) desc",
						BigDecimal.class, searchToken, searchToken, searchToken))
				.limit(0, maxResultSize);

		Result<Record4<String, Integer, String, BigDecimal>> items = searchSelect.fetch();
		List<SearchResult> result = new ArrayList<>();
		for (Record4<String, Integer, String, BigDecimal> item : items) {
			CodeAggregateType aggregateType = aggregateTypeEnum.getItem(item.value1());
			result.add(new SearchResult(null, aggregateType, item.value2(), item.value3(), item.value4()));
		}
		return result;
	}

}
