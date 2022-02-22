package fm.comunas.fm.building.service.api;

import fm.comunas.ddd.enums.adapter.api.jsonapi.dto.EnumeratedDto;
import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.ObjBuildingPartElement;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data()
@Builder(toBuilder = true)
public class ProjectionResult {

	private Integer startYear;
	private Integer duration;
	private List<RestorationElement> elementList;
	private List<ProjectionPeriod> periodList;

	@JsonIgnore
	private Map<EnumeratedDto, ObjBuildingPartElement> elementMap;
	@JsonIgnore
	private Map<String, List<ProjectionPeriod>> elementResultMap;

	public Integer getEndYear() {
		return this.startYear + this.duration;
	}

	public ObjBuildingPartElement getElement(EnumeratedDto enumerated) {
		return this.elementMap.get(enumerated);
	}

	public ObjBuilding getBuilding(EnumeratedDto enumerated) {
		return (ObjBuilding) this.getElement(enumerated).getMeta().getAggregate();
	}

}
