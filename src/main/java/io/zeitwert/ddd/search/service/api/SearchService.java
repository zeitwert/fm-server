
package io.zeitwert.ddd.search.service.api;

import java.util.List;

import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.session.model.RequestContext;

public interface SearchService {

	List<SearchResult> find(RequestContext requestCtx, String searchText, int maxResultSize);

	List<SearchResult> find(RequestContext requestCtx, List<String> itemTypes, String searchText, int maxResultSize);

}
