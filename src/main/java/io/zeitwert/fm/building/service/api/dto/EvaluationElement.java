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
	private Integer condition;
	private Color conditionColor;

	private Integer restorationYear;
	private Integer restorationCosts;

}
