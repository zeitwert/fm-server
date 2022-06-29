
package io.zeitwert.fm.app.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeRatingResponse {

	private Integer buildingId;
	private String buildingName;
	private String buildingOwner;
	private String buildingAddress;

	private String ratingDate;
	private String ratingUser;

}
