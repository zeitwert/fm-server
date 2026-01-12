package io.zeitwert.app.search.service.api.impl

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.enums.CodeAggregateTypeEnum
import io.zeitwert.app.ddd.model.SearchResult
import io.zeitwert.app.ddd.service.api.SearchService
import io.zeitwert.app.obj.model.db.tables.Obj
import io.zeitwert.app.search.model.db.Tables
import io.zeitwert.app.search.model.db.tables.ItemSearch
import io.zeitwert.app.search.model.db.tables.records.ItemSearchRecord
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.sql.ddd.util.SqlUtils
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record6
import org.jooq.SelectWithTiesAfterOffsetStep
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service("searchService")
class SearchServiceImpl internal constructor(
	private val dslContext: DSLContext,
	private val kernelContext: KernelContext,
) : SearchService,
	SqlUtils.SearchConditionProvider {

	override fun storeSearch(
		aggregate: Aggregate,
		texts: List<String?>,
		tokens: List<String?>,
	) {
		val allTexts = texts.filter { it != null }.joinToString(" ").lowercase(Locale.getDefault())
		val allTokens = tokens.filter { it != null }.joinToString(" ").lowercase(Locale.getDefault())
		val allTextsAndTokens = ("$allTexts $allTokens").trim { it <= ' ' }
		val aggregateType = aggregate.meta.repository.aggregateType.id
		val aggregateId = aggregate.id as Int
		val id = "$aggregateType:$aggregateId"
		dslContext
			.delete<ItemSearchRecord?>(ITEM_SEARCH)
			.where(ITEM_SEARCH.ID.eq(id))
			.execute()
		dslContext
			.insertInto<ItemSearchRecord?, String?, String?, Int?, String?, String?, String?>(
				ITEM_SEARCH,
				ITEM_SEARCH.ID,
				ITEM_SEARCH.ITEM_TYPE_ID,
				ITEM_SEARCH.ITEM_ID,
				ITEM_SEARCH.A_SIMPLE,
				ITEM_SEARCH.B_GERMAN,
				ITEM_SEARCH.B_ENGLISH,
			).values(
				id,
				aggregateType,
				aggregateId,
				allTokens,
				allTextsAndTokens,
				allTextsAndTokens,
			).execute()
	}

	override fun findOne(
		sessionContext: SessionContext,
		itemType: String,
		searchText: String,
	): SearchResult? {
		val results = find(sessionContext, listOf(itemType), searchText, 1)
		return if (results.isNotEmpty() && results[0].rank.toDouble() > 0.5) results[0] else null
	}

	override fun find(
		sessionContext: SessionContext,
		searchText: String,
		maxResultSize: Int,
	): List<SearchResult> = find(sessionContext, emptyList(), searchText, maxResultSize)

	override fun find(
		sessionContext: SessionContext,
		itemTypes: List<String>?,
		searchToken: String,
		maxResultSize: Int,
	): List<SearchResult> {
		val tenantId = sessionContext.tenantId as Int
		val accountId = sessionContext.accountId as Int?

		val kernelTenantId = kernelContext.kernelTenantId as Int
		val tenantCondition: Condition = if (kernelContext.isKernelTenant(tenantId)) {
			DSL.trueCondition()
		} else {
			OBJ.TENANT_ID.eq(tenantId).or(OBJ.TENANT_ID.eq(kernelTenantId))
		}
		val accountCondition: Condition = if (accountId == null) {
			DSL.trueCondition()
		} else {
			OBJ.ACCOUNT_ID.isNull().or(OBJ.ACCOUNT_ID.eq(accountId))
		}
		val itemTypeCondition = if (itemTypes != null && itemTypes.size > 0) {
			ITEM_SEARCH.ITEM_TYPE_ID.`in`(itemTypes)
		} else {
			DSL.noCondition()
		}
		val searchText = listOf(
			*searchToken
				.split(" ".toRegex())
				.dropLastWhile { it.isEmpty() }
				.toTypedArray(),
		).joinToString(" & ") { "$it:*" }
		val searchCondition = DSL
			.noCondition()
			.or("search_key @@ plainto_tsquery('simple', ?)", searchToken)
			.or("search_key @@ to_tsquery('german', ?)", searchText)

		val searchSelect: SelectWithTiesAfterOffsetStep<Record6<Int?, Int?, String?, Int?, String?, BigDecimal?>?> =
			dslContext
				.select<Int?, Int?, String?, Int?, String?, BigDecimal?>(
					OBJ.TENANT_ID,
					OBJ.ACCOUNT_ID,
					ITEM_SEARCH.ITEM_TYPE_ID,
					OBJ.ID,
					OBJ.CAPTION,
					DSL.field<BigDecimal?>(
						"(ts_rank(search_key, plainto_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?)))",
						BigDecimal::class.java,
						searchToken,
						searchText,
					),
				).from(ITEM_SEARCH)
				.join(OBJ)
				.on(OBJ.ID.eq(ITEM_SEARCH.ITEM_ID))
				.where(DSL.and(searchCondition, tenantCondition, accountCondition, itemTypeCondition))
				.orderBy<BigDecimal?>(
					DSL.field<BigDecimal?>(
						"(ts_rank(search_key, plainto_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?))) desc",
						BigDecimal::class.java,
						searchToken,
						searchText,
					),
				).limit(0, maxResultSize)

		val items = searchSelect.fetch()
		val result: MutableList<SearchResult> = mutableListOf()
		for (item in items) {
			if (item == null) {
				continue
			}
			val aggregateType = CodeAggregateTypeEnum.getAggregateType(item.value3()!!)
			val searchResult = SearchResult(
				tenantId = item.value1()!!,
				aggregateType = aggregateType,
				id = item.value4()!!,
				caption = item.value5() ?: "Unbekannt",
				rank = item.value6()!!,
			)
			result.add(searchResult)
		}
		return result
	}

	override fun applySearch(
		idField: Field<Int>,
		searchText: String?,
	): Condition = searchCondition(idField, searchText)

	private fun searchCondition(
		idField: Field<Int>,
		searchToken: String?,
	): Condition {
		if (searchToken.isNullOrBlank()) {
			return DSL.trueCondition()
		}
		val searchText = listOf(
			*searchToken
				.split(" ".toRegex())
				.dropLastWhile { it.isEmpty() }
				.toTypedArray(),
		).joinToString(" & ") { "$it:*" }
		return idField.`in`(
			DSL
				.select<Int?>(ITEM_SEARCH.ITEM_ID)
				.from(ITEM_SEARCH)
				.where(
					DSL
						.noCondition()
						.or("search_key @@ plainto_tsquery('simple', ?)", searchToken)
						.or("search_key @@ to_tsquery('german', ?)", searchText),
				).orderBy<BigDecimal?>(
					DSL.field<BigDecimal?>(
						"(ts_rank(search_key, plainto_tsquery('simple', ?)) + ts_rank(search_key, to_tsquery('german', ?))) desc",
						BigDecimal::class.java,
						searchToken,
						searchText,
					),
				),
		)
	}

	companion object {

		private val ITEM_SEARCH: ItemSearch = Tables.ITEM_SEARCH
		private val OBJ: Obj = io.zeitwert.app.obj.model.db.Tables.OBJ
	}

}
