
package io.dddrive.search.service.api;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;

import io.crnk.core.queryspec.FilterSpec;
import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.search.model.SearchResult;

public interface SearchService {

	/**
	 * Store the search texts and tokens.
	 *
	 * @param aggregate aggregate to store
	 * @param texts     list of texts to be stored
	 * @param tokens    list of tokens to be stored
	 */
	void storeSearch(Aggregate aggregate, List<String> texts, List<String> tokens);

	List<SearchResult> find(RequestContext requestCtx, String searchText, int maxResultSize);

	List<SearchResult> find(RequestContext requestCtx, List<String> itemTypes, String searchText, int maxResultSize);

	Condition searchFilter(Field<Integer> idField, FilterSpec filter);

}
