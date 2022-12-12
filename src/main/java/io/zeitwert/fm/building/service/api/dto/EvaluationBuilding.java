package io.zeitwert.fm.building.service.api.dto;

import java.awt.Color;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class EvaluationBuilding {

	private Integer id;
	private String name;
	private String description;
	private String buildingNr;
	private String address;

	private Integer insuredValue;
	private Integer insuredValueYear;

	private Integer ratingYear;

	private Integer condition; // zn100
	private Color conditionColor;

}
