package fm.comunas.fm.building.adapter.api.rest.dto;

import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
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
