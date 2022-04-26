
package io.zeitwert.fm.building.adapter.api.rest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.common.model.enums.CodeCurrencyEnum;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferElementDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.TransferMetaDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservationEnum;

import javax.servlet.ServletException;

@RestController("buildingFileTransferController")
@RequestMapping("/transfer/building/buildings")
public class BuildingFileTransferController {

	private static final String AGGREGATE = "zeitwert/building";
	private static final String VERSION = "1.0";

	@Autowired
	private ObjBuildingRepository repo;

	@Autowired
	SessionInfo sessionInfo;

	@GetMapping("/{id}")
	protected ResponseEntity<BuildingTransferDto> exportBuilding(@PathVariable("id") Integer id)
			throws ServletException, IOException {
		Optional<ObjBuilding> maybeBuilding = this.repo.get(sessionInfo, id);
		if (maybeBuilding.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		ObjBuilding building = maybeBuilding.get();
		BuildingTransferDto export = this.getTransferDto(building);
		String fileName = this.getFileName(building);
		ResponseEntity<BuildingTransferDto> response = ResponseEntity.ok()
				.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"") // mark file for download
				.body(export);
		return response;
	}

	@PostMapping
	protected ResponseEntity<BuildingTransferDto> importBuilding(@RequestBody BuildingTransferDto dto)
			throws ServletException, IOException {
		Integer accountId = sessionInfo.getAccountId();
		if (accountId == null) {
			return ResponseEntity.badRequest().build();
		} else if (!dto.getMeta().getAggregate().equals(AGGREGATE)) {
			return ResponseEntity.unprocessableEntity().build();
		} else if (!dto.getMeta().getVersion().equals(VERSION)) {
			return ResponseEntity.unprocessableEntity().build();
		}
		ObjBuilding building = repo.create(sessionInfo);
		AppContext appContext = building.getMeta().getAppContext();
		building.setAccountId(accountId);
		//@formatter:off
		building.setOwner(sessionInfo.getUser());
		building.setName(dto.getName());
		building.setDescription(dto.getDescription());
		building.setBuildingNr(dto.getBuildingNr());
		building.setBuildingInsuranceNr(dto.getBuildingInsuranceNr());
		building.setPlotNr(dto.getPlotNr());
		building.setNationalBuildingId(dto.getNationalBuildingId());
		building.setHistoricPreservation(dto.getHistoricPreservation() != null ? appContext.getEnumerated(CodeHistoricPreservationEnum.class, dto.getHistoricPreservation()) : null);
		building.setStreet(dto.getStreet());
		building.setZip(dto.getZip());
		building.setCity(dto.getCity());
		building.setCountry(dto.getCountry() != null ? appContext.getEnumerated(CodeCountryEnum.class, dto.getCountry()) : null);
		building.setCurrency(dto.getCurrency() != null ? appContext.getEnumerated(CodeCurrencyEnum.class, dto.getCurrency()) : null);
		building.setVolume(dto.getVolume());
		building.setAreaGross(dto.getAreaGross());
		building.setAreaNet(dto.getAreaNet());
		building.setNrOfFloorsAboveGround(dto.getNrOfFloorsAboveGround());
		building.setNrOfFloorsBelowGround(dto.getNrOfFloorsBelowGround());
		building.setBuildingType(dto.getBuildingType() != null ? appContext.getEnumerated(CodeBuildingTypeEnum.class, dto.getBuildingType()) : null);
		building.setBuildingSubType(dto.getBuildingSubType() != null ? appContext.getEnumerated(CodeBuildingSubTypeEnum.class, dto.getBuildingSubType()) : null);
		building.setBuildingYear(dto.getBuildingYear());
		building.setInsuredValue(dto.getInsuredValue());
		building.setInsuredValueYear(dto.getInsuredValueYear());
		building.setNotInsuredValue(dto.getNotInsuredValue());
		building.setNotInsuredValueYear(dto.getNotInsuredValueYear());
		building.setThirdPartyValue(dto.getThirdPartyValue());
		building.setThirdPartyValueYear(dto.getThirdPartyValueYear());
		building.setBuildingPartCatalog(dto.getBuildingPartCatalog() != null ? appContext.getEnumerated(CodeBuildingPartCatalogEnum.class, dto.getBuildingPartCatalog()) : null);
		building.setBuildingMaintenanceStrategy(dto.getBuildingMaintenanceStrategy() != null ? appContext.getEnumerated(CodeBuildingMaintenanceStrategyEnum.class, dto.getBuildingMaintenanceStrategy()) : null);
		dto.getElements().forEach((dtoElement) -> {
			CodeBuildingPart buildingPart = appContext.getEnumerated(CodeBuildingPartEnum.class, dtoElement.getBuildingPart());
			ObjBuildingPartElement element = building.addElement(buildingPart);
			element.setDescription(dtoElement.getDescription());
			element.setValuePart(dtoElement.getValuePart());
			element.setCondition(dtoElement.getCondition());
			element.setConditionYear(dtoElement.getConditionYear());
			element.setStrain(dtoElement.getStrain());
			element.setStrength(dtoElement.getStrength());
		});
		repo.store(building);
		BuildingTransferDto export = this.getTransferDto(building);
		ResponseEntity<BuildingTransferDto> response = ResponseEntity.ok().body(export);
		//@formatter:on
		return response;
	}

	private BuildingTransferDto getTransferDto(ObjBuilding building) {
		//@formatter:off
		TransferMetaDto meta = TransferMetaDto
			.builder()
				.aggregate(AGGREGATE)
				.version(VERSION)
			.build();
		List<BuildingTransferElementDto> elements = building.getElementList().stream().map(e -> {
			return BuildingTransferElementDto
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
		BuildingTransferDto export = BuildingTransferDto
			.builder()
				.meta(meta)
				.id(building.getId())
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
		//@formatter:on
		return export;
	}

	private String getFileName(ObjBuilding building) {
		return (building.getAccount() != null ? building.getAccount().getName() + " " : "") + building.getName() + ".zwbd";
	}

}
