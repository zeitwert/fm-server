package io.zeitwert.fm.search.adapter.api.rest.dto;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import io.dddrive.search.model.SearchResult;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SearchResultDto {

	private Integer tenantId;
	private EnumeratedDto itemType;
	private String id;
	private String caption;
	private BigDecimal rank;

	public static SearchResultDto fromItem(SearchResult searchResult) {
		// @formatter:off
		return SearchResultDto.builder()
			.tenantId(searchResult.getTenantId())
			.itemType(EnumeratedDto.fromEnum(searchResult.getAggregateType()))
			.id(searchResult.getId().toString())
			.caption(searchResult.getCaption())
			.rank(searchResult.getRank())
			.build();
		// @formatter:on
	}

}
