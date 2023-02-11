
package io.zeitwert.ddd.search.service.api;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.search.model.SearchResult;

public interface SearchService {

	/**
	 * Store the search texts and tokens.
	 *
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens);

	List<SearchResult> find(String searchText, int maxResultSize);

	List<SearchResult> find(List<String> itemTypes, String searchText, int maxResultSize);

}
