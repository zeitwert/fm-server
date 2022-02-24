
package io.zeitwert.ddd.search.service.api;

import java.util.List;

import io.zeitwert.ddd.search.model.SearchResult;

public interface SearchService {

	List<SearchResult> find(String searchText, int maxResultSize);

	List<SearchResult> find(List<String> itemTypes, String searchText, int maxResultSize);

}
