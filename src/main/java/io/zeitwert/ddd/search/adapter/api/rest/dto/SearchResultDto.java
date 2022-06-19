package io.zeitwert.ddd.search.adapter.api.rest.dto;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.dto.ObjTenantDto;
import io.zeitwert.ddd.oe.adapter.api.jsonapi.impl.ObjTenantDtoBridge;
import io.zeitwert.ddd.search.model.SearchResult;
import io.zeitwert.ddd.session.model.SessionInfo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SearchResultDto {

	private ObjTenantDto tenant;
	private EnumeratedDto itemType;
	private String id;
	private String caption;
	private BigDecimal rank;

	public static SearchResultDto fromItem(SearchResult searchResult, SessionInfo sessionInfo) {
		ObjTenantDtoBridge tenantBridge = ObjTenantDtoBridge.getInstance();
		// @formatter:off
		return SearchResultDto.builder()
			.tenant(tenantBridge.fromAggregate(searchResult.getTenant(), sessionInfo))
			.itemType(EnumeratedDto.fromEnum(searchResult.getAggregateType()))
			.id(searchResult.getId())
			.caption(searchResult.getCaption())
			.rank(searchResult.getRank())
			.build();
		// @formatter:on
	}

}
