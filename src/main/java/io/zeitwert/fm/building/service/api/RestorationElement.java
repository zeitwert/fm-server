package io.zeitwert.fm.building.service.api;

import io.zeitwert.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class RestorationElement {

	private EnumeratedDto element;
	private EnumeratedDto building;
	private EnumeratedDto buildingPart;

	private double restorationCosts;

}
