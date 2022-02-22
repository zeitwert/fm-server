
package fm.comunas.ddd.search.adapter.api.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.search.adapter.api.rest.dto.SearchResultDto;
import fm.comunas.ddd.search.model.SearchResult;
import fm.comunas.ddd.search.service.api.SearchService;
import fm.comunas.ddd.util.CustomFilters;

@RestController("searchController")
@RequestMapping("/api/search")
public class SearchController {

	private static final Integer SEARCH_RESULT_SIZE = 10;

	private final SearchService searchService;

	@Autowired
	SearchController(AppContext appContext, SearchService searchService) {
		this.searchService = searchService;
		appContext.addFilterOperator(CustomFilters.IN);
	}

	@GetMapping()
	public ResponseEntity<List<SearchResultDto>> find(@RequestParam String searchText,
			@RequestParam(required = false) List<String> itemTypes) {
		List<SearchResult> items = this.searchService.find(itemTypes, searchText, SEARCH_RESULT_SIZE);
		Collections.sort(items, Collections.reverseOrder());
		return ResponseEntity.ok(items.stream().map(sr -> SearchResultDto.fromItem(sr)).limit(SEARCH_RESULT_SIZE).toList());
	}

}
