
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
import org.jooq.impl.DSL;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import io.crnk.core.queryspec.FilterSpec;
import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.enums.CodeAggregateType;
import io.dddrive.ddd.model.enums.CodeAggregateTypeEnum;
import io.dddrive.jooq.util.SqlUtils;
import io.dddrive.search.model.SearchResult;
import io.dddrive.search.service.api.SearchService;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.obj.model.db.tables.Obj;
import io.zeitwert.fm.search.model.db.Tables;
import io.zeitwert.fm.search.model.db.tables.ItemSearch;

@Service("searchService")
@DependsOn("appContext")
public class SearchServiceImpl implements SearchService {

	private static final ItemSearch ITEM_SEARCH = Tables.ITEM_SEARCH;
	private static final Obj OBJ = io.zeitwert.fm.obj.model.db.Tables.OBJ;

	private final DSLContext dslContext;

	SearchServiceImpl(DSLContext dslContext) {
		this.dslContext = dslContext;
		SqlUtils.setSearchService(this);
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

	@Override
	public List<SearchResult> find(RequestContext requestCtx, String searchText, int maxResultSize) {
		return this.find(requestCtx, List.of(), searchText, maxResultSize);
	}

	@Override
	public List<SearchResult> find(RequestContext requestCtx, List<String> itemTypes, String searchText,
			int maxResultSize) {

		Condition tenantCondition = OBJ.TENANT_ID.eq(requestCtx.getTenantId());
		Condition accountCondition = OBJ.ACCOUNT_ID.isNull()
				.or(OBJ.ACCOUNT_ID.eq(((RequestContextFM) requestCtx).getAccountId()));
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
			CodeAggregateType aggregateType = CodeAggregateTypeEnum.getAggregateType(item.value1());
			result.add(new SearchResult(null, aggregateType, item.value2(), item.value3(), item.value4()));
		}
		return result;
	}

	@Override
	public Condition searchFilter(Field<Integer> idField, FilterSpec filter) {
		String searchText = filter.getValue();
		String searchToken = "'" + searchText + "':*";
		return idField.in(
				DSL
						.select(ITEM_SEARCH.ITEM_ID)
						.from(ITEM_SEARCH)
						.where(
								DSL.noCondition()
										.or("search_key @@ to_tsquery('simple', ?)", searchToken)
										.or("search_key @@ to_tsquery('german', ?)", searchToken)
										.or("search_key @@ to_tsquery('english', ?)", searchToken))
						.orderBy(DSL.field(
								"(ts_rank(search_key, to_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)) + ts_rank(search_key, to_tsquery('english', ?))) desc",
								BigDecimal.class, searchToken, searchToken, searchToken)));
	}

}
