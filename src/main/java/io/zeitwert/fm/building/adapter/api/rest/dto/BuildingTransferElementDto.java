package io.zeitwert.fm.building.adapter.api.rest.dto;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class BuildingTransferElementDto {

	private String buildingPart;
	private Integer valuePart;
	private Integer condition;
	private Integer conditionYear;
	private Integer strain;
	private Integer strength;
	private String description;
	private String conditionDescription;
	private String measureDescription;
	// private String[] conditionDescriptions;
	// private String[] materialDescriptions;
	// private String[] measureDescriptions;

}
