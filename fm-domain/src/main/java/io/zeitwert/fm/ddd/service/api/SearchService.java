
package io.zeitwert.fm.ddd.service.api;

import java.util.List;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.Aggregate;
import io.zeitwert.fm.ddd.model.SearchResult;

public interface SearchService {

	/**
	 * Store the search texts and tokens.
	 *
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens);

	SearchResult findOne(RequestContext requestCtx, String itemType, String searchText);

	List<SearchResult> find(RequestContext requestCtx, String searchText, int maxResultSize);

	List<SearchResult> find(RequestContext requestCtx, List<String> itemTypes, String searchText, int maxResultSize);

}
