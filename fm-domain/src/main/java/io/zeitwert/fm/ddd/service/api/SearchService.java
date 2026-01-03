package io.zeitwert.fm.ddd.service.api;

import dddrive.ddd.core.model.Aggregate;
import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.fm.ddd.model.SearchResult;

import java.util.List;

public interface SearchService {

	/**
	 * Store the search texts and tokens.
	 *
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens);

	SearchResult findOne(SessionContext sessionContext, String itemType, String searchText);

	List<SearchResult> find(SessionContext sessionContext, String searchText, int maxResultSize);

	List<SearchResult> find(SessionContext sessionContext, List<String> itemTypes, String searchText, int maxResultSize);

}
