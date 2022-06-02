package io.zeitwert.fm.building.service.api.dto;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class EvaluationPeriod {

	private Integer year;

	private Integer originalValue;
	private Integer timeValue;

	private Integer maintenanceCosts;
	private Integer restorationCosts;
	private String restorationElement;

	private Integer totalCosts;
	private Integer aggrCosts;

}
