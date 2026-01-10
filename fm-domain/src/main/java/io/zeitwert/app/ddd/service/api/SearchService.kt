package io.zeitwert.app.ddd.service.api

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.ddd.model.SearchResult
import io.zeitwert.app.session.model.SessionContext

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
