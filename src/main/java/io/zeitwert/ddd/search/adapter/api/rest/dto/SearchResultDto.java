package io.zeitwert.ddd.search.adapter.api.rest.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.fm.oe.adapter.api.jsonapi.impl.ObjTenantDtoAdapter;
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
		ObjTenantDtoAdapter tenantDtoAdapter = ObjTenantDtoAdapter.getInstance();
		// @formatter:off
		return SearchResultDto.builder()
			.tenant(tenantDtoAdapter.asEnumerated(searchResult.getTenant()))
			.itemType(EnumeratedDto.fromEnum(searchResult.getAggregateType()))
			.id(searchResult.getId())
			.caption(searchResult.getCaption())
			.rank(searchResult.getRank())
			.build();
		// @formatter:on
	}

}
