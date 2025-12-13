package io.zeitwert.fm.building.service.api.dto;

import java.awt.Color;

import io.zeitwert.fm.util.Formatter;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class EvaluationBuilding implements Comparable<EvaluationBuilding> {

	private Integer id;
	private String name;
	private String description;
	private String buildingNr;
	private String address;

	private Integer insuredValue;
	private Integer relativeValue;
	private Integer insuredValueYear;

	private Integer ratingYear;

	private Integer condition; // zn100
	private Color conditionColor;

	public String getFormattedInsuredValue() {
		return Formatter.INSTANCE.formatNumber(1000 * this.insuredValue);
	}

	public String getRelativeValue() {
		int weight = (int) Math.round(this.relativeValue * 70 / 100);
		return new String(new char[weight]).replace('\0', 'I');
	}

	@Override
	public int compareTo(EvaluationBuilding o) {
		int res = this.buildingNr.compareTo(o.buildingNr);
		if (res == 0) {
			res = this.name.compareTo(o.name);
		}
		return res;
	}

}
