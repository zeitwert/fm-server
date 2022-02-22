
package fm.comunas.ddd.search.service.api;

import java.util.List;

import fm.comunas.ddd.search.model.SearchResult;

public interface SearchService {

	List<SearchResult> find(String searchText, int maxResultSize);

	List<SearchResult> find(List<String> itemTypes, String searchText, int maxResultSize);

}
