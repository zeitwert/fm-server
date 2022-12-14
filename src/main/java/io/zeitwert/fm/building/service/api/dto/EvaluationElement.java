package io.zeitwert.fm.building.service.api.dto;

import lombok.Builder;
import lombok.Data;
import java.awt.Color;

import io.zeitwert.ddd.util.Formatter;

@Data()
@Builder
public class EvaluationElement {

	private String name;
	private String buildingName;
	private String elementName;
	private String description;

	private Integer weight;
	private Integer condition;
	private Color conditionColor;

	private Integer restorationYear;
	private Integer restorationCosts;

	private Integer shortTermCosts;
	private Integer midTermCosts;
	private Integer longTermCosts;

	public String getFormattedShortTermCosts() {
		return Formatter.INSTANCE.formatNumber(this.shortTermCosts);
	}

	public String getFormattedMidTermCosts() {
		return Formatter.INSTANCE.formatNumber(this.midTermCosts);
	}

	public String getFormattedLongTermCosts() {
		return Formatter.INSTANCE.formatNumber(this.longTermCosts);
	}

}
