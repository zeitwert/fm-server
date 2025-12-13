
package io.zeitwert.fm.search.adapter.api.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.fm.ddd.model.SearchResult;
import io.zeitwert.fm.ddd.service.api.SearchService;
import io.zeitwert.fm.search.adapter.api.rest.dto.SearchResultDto;

@RestController("searchController")
@RequestMapping("/rest/search")
public class SearchController {

	private static final Integer SEARCH_RESULT_SIZE = 10;

	@Autowired
	SearchService searchService;

	@Autowired
	RequestContext requestCtx;

	@GetMapping()
	public ResponseEntity<List<SearchResultDto>> find(
			@RequestParam String searchText,
			@RequestParam(required = false) List<String> itemTypes) {
		List<SearchResult> items = this.searchService.find(this.requestCtx, itemTypes, searchText, SEARCH_RESULT_SIZE);
		Collections.sort(items, Collections.reverseOrder());
		return ResponseEntity.ok(
				items.stream().limit(SEARCH_RESULT_SIZE).map(SearchResultDto::fromItem).toList());
	}

}
