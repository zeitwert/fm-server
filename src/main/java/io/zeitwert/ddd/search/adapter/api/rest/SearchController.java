
package io.zeitwert.ddd.search.adapter.api.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.search.adapter.api.rest.dto.SearchResultDto;
import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.search.service.api.SearchService;
import io.zeitwert.ddd.session.model.RequestContext;

@RestController("searchController")
@RequestMapping("/api/search")
public class SearchController {

	private static final Integer SEARCH_RESULT_SIZE = 10;

	private final SearchService searchService;

	private final RequestContext requestCtx;

	SearchController(SearchService searchService, RequestContext requestCtx) {
		this.searchService = searchService;
		this.requestCtx = requestCtx;
	}

	@GetMapping()
	public ResponseEntity<List<SearchResultDto>> find(@RequestParam String searchText,
			@RequestParam(required = false) List<String> itemTypes) {
		List<SearchResult> items = this.searchService.find(this.requestCtx, itemTypes, searchText, SEARCH_RESULT_SIZE);
		Collections.sort(items, Collections.reverseOrder());
		return ResponseEntity.ok(
				items.stream().limit(SEARCH_RESULT_SIZE).map(sr -> SearchResultDto.fromItem(sr, this.requestCtx)).toList());
	}

}
