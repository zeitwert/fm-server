
package io.zeitwert.fm.building.adapter.api.rest;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.oe.service.api.ObjUserCache;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferElementRatingDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.NoteTransferDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.TransferMetaDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatusEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservationEnum;
import io.zeitwert.fm.building.service.api.ObjBuildingCache;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteTypeEnum;

@RestController("buildingFileTransferController")
@RequestMapping("/rest/building/buildings")
public class BuildingImportExportController {

	private static final String AGGREGATE = "zeitwert/building";
	private static final String VERSION = "1.0";

	@Autowired
	private ObjBuildingCache buildingCache;

	@Autowired
	private ObjBuildingRepository buildingRepo;

	@Autowired
	private ObjUserCache userCache;

	@Autowired
	private ObjNoteRepository noteRepo;

	@Autowired
	RequestContext requestCtx;

	@GetMapping("/{id}")
	public ResponseEntity<BuildingTransferDto> exportBuilding(@PathVariable("id") Integer id)
			throws ServletException, IOException {
		ObjBuilding building = this.buildingCache.get(id);
		BuildingTransferDto export = this.getTransferDto(building);
		String fileName = this.getFileName(building);
		// mark file for download
		ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		return ResponseEntity.ok().headers(headers).body(export);
	}

	@PostMapping
	public ResponseEntity<BuildingTransferDto> importBuilding(@RequestBody BuildingTransferDto dto)
			throws ServletException, IOException {
		Integer accountId = this.requestCtx.getAccountId();
		if (accountId == null) {
			return ResponseEntity.badRequest().build();
		} else if (!AGGREGATE.equals(dto.getMeta().getAggregate())) {
			return ResponseEntity.unprocessableEntity().build();
		} else if (!VERSION.equals(dto.getMeta().getVersion())) {
			return ResponseEntity.unprocessableEntity().build();
		}
		ObjBuilding building = this.buildingRepo.create(this.requestCtx.getTenantId());
		building.setAccountId(accountId);
		this.fillFromDto(building, dto);
		this.buildingRepo.store(building);
		BuildingTransferDto export = this.getTransferDto(building);
		return ResponseEntity.ok().body(export);
	}

	private BuildingTransferDto getTransferDto(ObjBuilding building) {
		//@formatter:off
		TransferMetaDto meta = TransferMetaDto
			.builder()
				.aggregate(AGGREGATE)
				.version(VERSION)
				.createdByUser(building.getMeta().getCreatedByUser().getEmail())
				.createdAt(building.getMeta().getCreatedAt())
				.modifiedByUser(building.getMeta().getModifiedByUser() != null ? building.getMeta().getModifiedByUser().getEmail() : null)
				.modifiedAt(building.getMeta().getModifiedAt())
			.build();
		List<BuildingTransferElementRatingDto> elements = building.getCurrentRating().getElementList().stream().map(e -> {
			return BuildingTransferElementRatingDto
				.builder()
					.buildingPart(e.getBuildingPart().getId())
					.weight(e.getWeight())
					.condition(e.getCondition())
					.ratingYear(e.getRatingYear())
					.strain(e.getStrain())
					.strength(e.getStrength())
					.description(e.getDescription())
					.conditionDescription(e.getConditionDescription())
					.measureDescription(e.getMeasureDescription())
				.build();
		}).toList();
		List<NoteTransferDto> notes = building.getNotes().stream().map(note -> {
			return NoteTransferDto
				.builder()
					.subject(note.getSubject())
					.content(note.getContent())
					.isPrivate(note.getIsPrivate())
					.createdByUser(this.userCache.get(note.getCreatedByUserId()).getEmail())
					.createdAt(note.getCreatedAt())
					.modifiedByUser(note.getModifiedByUserId() != null ? this.userCache.get(note.getModifiedByUserId()).getEmail() : null)
					.modifiedAt(note.getModifiedAt())
				.build();
		}).toList();
		BuildingTransferDto export = BuildingTransferDto
			.builder()
				.meta(meta)
				.id(building.getId())
				.name(building.getName())
				.description(building.getDescription())
				.buildingNr(building.getBuildingNr())
				.buildingInsuranceNr(building.getInsuranceNr())
				.plotNr(building.getPlotNr())
				.nationalBuildingId(building.getNationalBuildingId())
				.historicPreservation(building.getHistoricPreservation() != null ? building.getHistoricPreservation().getId() : null)
				.street(building.getStreet())
				.zip(building.getZip())
				.city(building.getCity())
				.country(building.getCountry() != null ? building.getCountry().getId() : null)
				.geoAddress(building.getGeoAddress())
				.geoCoordinates(building.getGeoCoordinates())
				.geoZoom(building.getGeoZoom())
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
				.buildingPartCatalog(building.getCurrentRating().getPartCatalog() != null ? building.getCurrentRating().getPartCatalog().getId() : null)
				.buildingMaintenanceStrategy(building.getCurrentRating().getMaintenanceStrategy() != null ? building.getCurrentRating().getMaintenanceStrategy().getId() : null)
				.ratingStatus(building.getCurrentRating().getRatingStatus().getId())
				.ratingDate(building.getCurrentRating().getRatingDate())
				.ratingUser(building.getCurrentRating().getRatingUser() != null ? building.getCurrentRating().getRatingUser().getEmail() : null)
				.elements(elements)
				.notes(notes)
			.build();
		//@formatter:on
		return export;
	}

	private String getFileName(ObjBuilding building) {
		return (building.getAccount() != null ? building.getAccount().getName() + " " : "") + building.getName() + ".zwbd";
	}

	private void fillFromDto(ObjBuilding building, BuildingTransferDto dto) {
		AppContext appContext = building.getMeta().getAppContext();
		try {
			building.getMeta().disableCalc();

			//@formatter:off
			building.setOwner(this.requestCtx.getUser());
			building.setName(dto.getName());
			building.setDescription(dto.getDescription());
			building.setBuildingNr(dto.getBuildingNr());
			building.setInsuranceNr(dto.getBuildingInsuranceNr());
			building.setPlotNr(dto.getPlotNr());
			building.setNationalBuildingId(dto.getNationalBuildingId());
			building.setHistoricPreservation(CodeHistoricPreservationEnum.getHistoricPreservation(dto.getHistoricPreservation()));
			building.setStreet(dto.getStreet());
			building.setZip(dto.getZip());
			building.setCity(dto.getCity());
			building.setCountry(CodeCountryEnum.getCountry(dto.getCountry()));
			building.setGeoAddress(dto.getGeoAddress());
			building.setGeoCoordinates(dto.getGeoCoordinates());
			building.setGeoZoom(dto.getGeoZoom());
			building.setCurrency(CodeCurrencyEnum.getCurrency(dto.getCurrency()));
			building.setVolume(dto.getVolume());
			building.setAreaGross(dto.getAreaGross());
			building.setAreaNet(dto.getAreaNet());
			building.setNrOfFloorsAboveGround(dto.getNrOfFloorsAboveGround());
			building.setNrOfFloorsBelowGround(dto.getNrOfFloorsBelowGround());
			building.setBuildingType(CodeBuildingTypeEnum.getBuildingType(dto.getBuildingType()));
			building.setBuildingSubType(CodeBuildingSubTypeEnum.getBuildingSubType(dto.getBuildingSubType()));
			building.setBuildingYear(dto.getBuildingYear());
			building.setInsuredValue(dto.getInsuredValue());
			building.setInsuredValueYear(dto.getInsuredValueYear());
			building.setNotInsuredValue(dto.getNotInsuredValue());
			building.setNotInsuredValueYear(dto.getNotInsuredValueYear());
			building.setThirdPartyValue(dto.getThirdPartyValue());
			building.setThirdPartyValueYear(dto.getThirdPartyValueYear());
			final ObjBuildingPartRating rating = building.getCurrentRating() != null ? building.getCurrentRating() : building.addRating();
			rating.setPartCatalog(CodeBuildingPartCatalogEnum.getPartCatalog(dto.getBuildingPartCatalog()));
			rating.setMaintenanceStrategy(CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy(dto.getBuildingMaintenanceStrategy()));
			rating.setRatingStatus(CodeBuildingRatingStatusEnum.getRatingStatus(dto.getRatingStatus()));
			rating.setRatingDate(dto.getRatingDate());
			rating.setRatingUser(dto.getRatingUser() != null ? this.userCache.getByEmail(dto.getRatingUser()).get() : null);
			if (dto.getElements() != null) {
				dto.getElements().forEach((dtoElement) -> {
					CodeBuildingPart buildingPart = appContext.getEnumerated(CodeBuildingPartEnum.class, dtoElement.getBuildingPart());
					ObjBuildingPartElementRating element = rating.getElement(buildingPart);
					if (element == null) {
						element = rating.addElement(buildingPart);
					}
					element.setWeight(dtoElement.getWeight());
					element.setCondition(dtoElement.getCondition());
					//element.setRatingYear(dtoElement.getRatingYear());
					element.setStrain(dtoElement.getStrain());
					element.setStrength(dtoElement.getStrength());
					element.setDescription(dtoElement.getDescription());
					element.setConditionDescription(dtoElement.getConditionDescription());
					element.setMeasureDescription(dtoElement.getMeasureDescription());
				});
			}
			if (dto.getNotes() != null) {
				CodeNoteType noteType = CodeNoteTypeEnum.getNoteType("note");
				dto.getNotes().forEach((dtoNote) -> {
					ObjNote note = building.addNote(noteType);
					note.setSubject(dtoNote.getSubject());
					note.setContent(dtoNote.getContent());
					note.setIsPrivate(dtoNote.getIsPrivate());
					this.noteRepo.store(note);
				});
			}
			//@formatter:on

		} finally {
			building.getMeta().enableCalc();
			building.calcAll();
		}
	}

}
