package io.zeitwert.fm.building.adapter.api.rest.dto;

import io.dddrive.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class BuildingPartWeightDto {

	private EnumeratedDto part;
	private Integer weight;

	private Integer lifeTime20;
	private Integer lifeTime50;
	private Integer lifeTime70;
	private Integer lifeTime85;
	private Integer lifeTime95;
	private Integer lifeTime100;

}
