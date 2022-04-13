
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingElementExportDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingExportDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingRepository;

import javax.servlet.ServletException;

@RestController("buildingExportController")
@RequestMapping("/export/building/buildings")
public class BuildingExportController {

	@Autowired
	private ObjBuildingRepository repo;

	@GetMapping("/{id}")
	protected ResponseEntity<BuildingExportDto> exportBuilding(SessionInfo sessionInfo, @PathVariable("id") Integer id)
			throws ServletException, IOException {
		Optional<ObjBuilding> maybeBuilding = this.repo.get(sessionInfo, id);
		if (maybeBuilding.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		ObjBuilding building = maybeBuilding.get();
		//@formatter:off
		List<BuildingElementExportDto> elements = building.getElementList().stream().map(e -> {
			return BuildingElementExportDto
				.builder()
					.buildingPart(e.getBuildingPart().getId())
					.description(e.getDescription())
					.valuePart(e.getValuePart())
					.condition(e.getCondition())
					.conditionYear(e.getConditionYear())
					.strain(e.getStrain())
					.strength(e.getStrength())
				.build();
		}).toList();
		BuildingExportDto export = BuildingExportDto
			.builder()
				.name(building.getName())
				.description(building.getDescription())
				.buildingNr(building.getBuildingNr())
				.buildingInsuranceNr(building.getBuildingInsuranceNr())
				.plotNr(building.getPlotNr())
				.nationalBuildingId(building.getNationalBuildingId())
				.historicPreservation(building.getHistoricPreservation() != null ? building.getHistoricPreservation().getId() : null)
				.street(building.getStreet())
				.zip(building.getZip())
				.city(building.getCity())
				.country(building.getCountry() != null ? building.getCountry().getId() : null)
				.currency(building.getCurrency() != null ? building.getCurrency().getId() : null)
				.volume(building.getVolume())
				.areaGross(building.getAreaGross())
				.areaNet(building.getAreaNet())
				.nrOfFloorsAboveGround(building.getNrOfFloorsAboveGround())
				.nrOfFloorsBelowGround(building.getNrOfFloorsBelowGround())
				.buildingType(building.getBuildingType() != null ? building.getBuildingType().getId() : null)
				.buildingSubType(building.getBuildingSubType() != null ? building.getBuildingSubType().getId() : null)
				.buildingYear(building.getBuildingYear())
				.insuredValue(building.getInsuredValue())
				.insuredValueYear(building.getInsuredValueYear())
				.notInsuredValue(building.getNotInsuredValue())
				.notInsuredValueYear(building.getNotInsuredValueYear())
				.thirdPartyValue(building.getThirdPartyValue())
				.thirdPartyValueYear(building.getThirdPartyValueYear())
				.buildingPartCatalog(building.getBuildingPartCatalog() != null ? building.getBuildingPartCatalog().getId() : null)
				.buildingMaintenanceStrategy(building.getBuildingMaintenanceStrategy() != null ? building.getBuildingMaintenanceStrategy().getId() : null)
				.elements(elements)
			.build();
		ResponseEntity<BuildingExportDto> response = ResponseEntity.ok()
			.header("Content-Disposition", "attachment; filename=\"" + building.getAccount().getName() + " " + building.getName() + ".zwbd\"") // mark file for download
			.body(export);
		//@formatter:on
		return response;
	}

}
