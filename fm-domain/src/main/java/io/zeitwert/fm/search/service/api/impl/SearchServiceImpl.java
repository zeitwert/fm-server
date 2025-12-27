package io.zeitwert.fm.search.service.api.impl;

import dddrive.ddd.core.model.Aggregate;
import dddrive.ddd.core.model.enums.CodeAggregateType;
import dddrive.ddd.core.model.enums.CodeAggregateTypeEnum;
import io.crnk.core.queryspec.FilterSpec;
import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.persist.util.SqlUtils;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.ddd.model.SearchResult;
import io.zeitwert.fm.ddd.service.api.SearchService;
import io.zeitwert.fm.obj.model.db.tables.Obj;
import io.zeitwert.fm.oe.model.ObjTenantRepository;
import io.zeitwert.fm.search.model.db.Tables;
import io.zeitwert.fm.search.model.db.tables.ItemSearch;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("searchService")
public class SearchServiceImpl implements SearchService, SqlUtils.SearchConditionProvider {

	private static final ItemSearch ITEM_SEARCH = Tables.ITEM_SEARCH;
	private static final Obj OBJ = io.zeitwert.fm.obj.model.db.Tables.OBJ;

	private final DSLContext dslContext;

	SearchServiceImpl(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	@Override
	public void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens) {
		String allTexts = String.join(" ", texts.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTokens = String.join(" ", tokens.stream().filter(t -> t != null).toList()).toLowerCase();
		String allTextsAndTokens = (allTexts + " " + allTokens).trim();
		String aggregateType = aggregate.getMeta().getRepository().getAggregateType().getId();
		Integer aggregateId = (Integer) aggregate.getId();
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
	public SearchResult findOne(RequestContext requestCtx, String itemType, String searchText) {
		List<SearchResult> results = this.find(requestCtx, List.of(itemType), searchText, 1);
		return results.size() > 0 && results.get(0).getRank().doubleValue() > 0.5 ? results.get(0) : null;
	}

	@Override
	public List<SearchResult> find(RequestContext requestCtx, String searchText, int maxResultSize) {
		return this.find(requestCtx, List.of(), searchText, maxResultSize);
	}

	@Override
	public List<SearchResult> find(RequestContext requestCtx, List<String> itemTypes, String searchToken,
																 int maxResultSize) {

		Integer tenantId = (Integer) requestCtx.getTenantId();
		Integer accountId = ((RequestContextFM) requestCtx).getAccountId();

		Condition tenantCondition = tenantId == ObjTenantRepository.KERNEL_TENANT_ID
				? DSL.trueCondition()
				: OBJ.TENANT_ID.eq(tenantId).or(OBJ.TENANT_ID.eq(ObjTenantRepository.KERNEL_TENANT_ID));
		Condition accountCondition = accountId == null
				? DSL.trueCondition()
				: OBJ.ACCOUNT_ID.isNull().or(OBJ.ACCOUNT_ID.eq(accountId));
		Condition itemTypeCondition = itemTypes != null && itemTypes.size() > 0
				? ITEM_SEARCH.ITEM_TYPE_ID.in(itemTypes)
				: DSL.noCondition();
		String searchText = String.join(" & ", List.of(searchToken.split(" ")).stream().map(s -> s + ":*").toList());
		Condition searchCondition = DSL.noCondition()
				.or("search_key @@ plainto_tsquery('simple', ?)", searchToken)
				.or("search_key @@ to_tsquery('german', ?)", searchText);

		SelectWithTiesAfterOffsetStep<Record6<Integer, Integer, String, Integer, String, BigDecimal>> searchSelect = this.dslContext
				.select(
						OBJ.TENANT_ID,
						OBJ.ACCOUNT_ID,
						ITEM_SEARCH.ITEM_TYPE_ID,
						OBJ.ID,
						OBJ.CAPTION,
						DSL.field(
								"(ts_rank(search_key, plainto_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)))",
								BigDecimal.class, searchToken, searchText))
				.from(ITEM_SEARCH)
				.join(OBJ)
				.on(OBJ.ID.eq(ITEM_SEARCH.ITEM_ID))
				.where(DSL.and(searchCondition, tenantCondition, accountCondition, itemTypeCondition))
				.orderBy(DSL.field(
						"(ts_rank(search_key, plainto_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?))) desc",
						BigDecimal.class, searchToken, searchText))
				.limit(0, maxResultSize);

		Result<Record6<Integer, Integer, String, Integer, String, BigDecimal>> items = searchSelect.fetch();
		List<SearchResult> result = new ArrayList<>();
		for (Record6<Integer, Integer, String, Integer, String, BigDecimal> item : items) {
			CodeAggregateType aggregateType = CodeAggregateTypeEnum.getAggregateType(item.value3());
			SearchResult searchResult = SearchResult.builder()
					.tenantId(item.value1())
					.aggregateType(aggregateType)
					.id(item.value4())
					.caption(item.value5())
					.rank(item.value6())
					.build();
			result.add(searchResult);
		}

		return result;
	}

	@Override
	public Condition apply(Field<Integer> idField, FilterSpec filter) {
		return this.searchCondition(idField, filter);
	}

	private Condition searchCondition(Field<Integer> idField, FilterSpec filter) {
		String searchToken = filter.getValue();
		String searchText = String.join(" & ", List.of(searchToken.split(" ")).stream().map(s -> s + ":*").toList());
		return idField.in(
				DSL
						.select(ITEM_SEARCH.ITEM_ID)
						.from(ITEM_SEARCH)
						.where(
								DSL.noCondition()
										.or("search_key @@ plainto_tsquery('simple', ?)", searchToken)
										.or("search_key @@ to_tsquery('german', ?)", searchText))
						.orderBy(DSL.field(
								"(ts_rank(search_key, plainto_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?))) desc",
								BigDecimal.class, searchToken, searchText)));
	}

}
