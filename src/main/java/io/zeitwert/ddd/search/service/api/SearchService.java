
package io.zeitwert.ddd.search.service.api;

import java.util.List;

import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.session.model.SessionInfo;

public interface SearchService {

	List<SearchResult> find(SessionInfo sessionInfo, String searchText, int maxResultSize);

	List<SearchResult> find(SessionInfo sessionInfo, List<String> itemTypes, String searchText, int maxResultSize);

}
