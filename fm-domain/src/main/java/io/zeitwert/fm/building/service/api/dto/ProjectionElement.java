package io.zeitwert.fm.building.service.api.dto;

import io.zeitwert.dddrive.ddd.api.rest.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class ProjectionElement {

	private EnumeratedDto element;
	private EnumeratedDto building;
	private EnumeratedDto buildingPart;

	private double restorationCosts;

}
