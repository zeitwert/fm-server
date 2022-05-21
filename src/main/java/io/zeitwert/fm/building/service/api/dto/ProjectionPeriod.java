package io.zeitwert.fm.building.service.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data()
@Builder
public class ProjectionPeriod {

	private int year;
	private double originalValue;
	private double timeValue;
	private double restorationCosts;
	private List<RestorationElement> restorationElements;

	private double techPart;
	private double techRate;
	private double maintenanceRate;
	private double maintenanceCosts;

}
