package io.zeitwert.fm.building.service.api.dto;

import lombok.Builder;
import lombok.Data;
import java.awt.Color;

@Data()
@Builder
public class EvaluationElement {

	private String name;
	private String description;

	private Integer weight;
	private Integer rating;
	private Color ratingColor;

	private Integer restorationYear;
	private Integer restorationCosts;

}
