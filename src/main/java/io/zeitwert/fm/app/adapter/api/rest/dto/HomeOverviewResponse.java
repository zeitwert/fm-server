
package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeOverviewResponse {

	private Integer accountId;
	private String accountName;
	private String accountImageUrl;

	private Integer buildingCount;
	private Integer portfolioCount;
	private Integer ratingCount;
	private Integer insuranceValue;
	private Integer timeValue;

	private Integer shortTermRenovationCosts;
	private Integer midTermRenovationCosts;

}
