package io.zeitwert.fm.search.adapter.api.rest.dto;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.dddrive.search.model.SearchResult;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SearchResultDto {

	private EnumeratedDto tenant;
	private EnumeratedDto itemType;
	private String id;
	private String caption;
	private BigDecimal rank;

	public static SearchResultDto fromItem(SearchResult searchResult) {
		// @formatter:off
		return SearchResultDto.builder()
			.tenant(EnumeratedDto.fromAggregate(searchResult.getTenant()))
			.itemType(EnumeratedDto.fromEnum(searchResult.getAggregateType()))
			.id(searchResult.getId())
			.caption(searchResult.getCaption())
			.rank(searchResult.getRank())
			.build();
		// @formatter:on
	}

}
