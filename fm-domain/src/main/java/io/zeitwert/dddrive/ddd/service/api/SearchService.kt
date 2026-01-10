package io.zeitwert.dddrive.ddd.service.api

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.ddd.model.SearchResult

interface SearchService {

	/**
	 * Store the search texts and tokens.
	 *
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	fun storeSearch(
		aggregate: Aggregate,
		texts: List<String?>,
		tokens: List<String?>,
	)

	fun findOne(
		sessionContext: SessionContext,
		itemType: String,
		searchText: String,
	): SearchResult?

	fun find(
		sessionContext: SessionContext,
		searchText: String,
		maxResultSize: Int,
	): List<SearchResult>

	fun find(
		sessionContext: SessionContext,
		itemTypes: List<String>?,
		searchText: String,
		maxResultSize: Int,
	): List<SearchResult>

}
