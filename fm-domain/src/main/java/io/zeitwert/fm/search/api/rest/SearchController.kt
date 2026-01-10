package io.zeitwert.fm.search.api.rest

import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.dddrive.ddd.model.SearchResult
import io.zeitwert.dddrive.ddd.service.api.SearchService
import io.zeitwert.fm.search.api.rest.dto.SearchResultDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController("searchController")
@RequestMapping("/rest/search")
class SearchController {

	@Autowired
	lateinit var searchService: SearchService

	@Autowired
	lateinit var sessionContext: SessionContext

	@GetMapping
	fun find(
		@RequestParam searchText: String,
		@RequestParam(required = false) itemTypes: List<String>,
	): ResponseEntity<List<SearchResultDto>> {
		val items: List<SearchResult> = searchService.find(sessionContext, itemTypes, searchText, SEARCH_RESULT_SIZE)
		Collections.sort(items, Collections.reverseOrder())
		return ResponseEntity.ok(
			items
				.stream()
				.limit(SEARCH_RESULT_SIZE.toLong())
				.map {
					SearchResultDto(
						tenantId = it.tenantId,
						itemType = EnumeratedDto.of(it.aggregateType)!!,
						id = it.id.toString(),
						caption = it.caption,
						rank = it.rank,
					)
				}.toList(),
		)
	}

	companion object {

		private const val SEARCH_RESULT_SIZE = 10
	}

}
