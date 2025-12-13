
package io.zeitwert.fm.building.adapter.api.rest;

import io.dddrive.core.ddd.model.RepositoryDirectory;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import jakarta.servlet.ServletException;

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

import java.time.OffsetDateTime;

import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferElementRatingDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.NoteTransferDto;
import io.zeitwert.fm.building.adapter.api.rest.dto.TransferMetaDto;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType;
import io.zeitwert.fm.building.model.enums.CodeBuildingType;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.enums.CodeCountry;

@RestController("buildingFileTransferController")
@RequestMapping("/rest/building/buildings")
public class BuildingImportExportController {

	private static final String AGGREGATE = "zeitwert/building";
	private static final String VERSION = "1.0";

	@Autowired
	private RepositoryDirectory directory;

	@Autowired
	private ObjBuildingRepository buildingRepo;

	@Autowired
	private ObjUserFMRepository userRepo;

	@Autowired
	private ObjNoteRepository noteRepo;

	@Autowired
	RequestContextFM requestCtx;

	@GetMapping("/{id}")
	public ResponseEntity<BuildingTransferDto> exportBuilding(@PathVariable("id") Integer id)
			throws ServletException, IOException {
		ObjBuilding building = this.buildingRepo.get(id);
		BuildingTransferDto export = this.getTransferDto(building);
		String fileName = this.getFileName(building);
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
		Object userId = this.requestCtx.getUser().getId();
		OffsetDateTime timestamp = OffsetDateTime.now();
		ObjBuilding building = this.buildingRepo.create(this.requestCtx.getTenantId(), userId, timestamp);
		building.setAccountId(accountId);
		this.fillFromDto(building, dto);
		this.buildingRepo.store(building, userId, timestamp);
		BuildingTransferDto export = this.getTransferDto(building);
		return ResponseEntity.ok().body(export);
	}

	private BuildingTransferDto getTransferDto(ObjBuilding building) {
		TransferMetaDto meta = TransferMetaDto
				.builder()
				.aggregate(AGGREGATE)
				.version(VERSION)
				.createdByUser(building.getMeta().getCreatedByUser().getEmail())
				.createdAt(building.getMeta().getCreatedAt())
				.modifiedByUser(building.getMeta().getModifiedByUser() != null
						? building.getMeta().getModifiedByUser().getEmail()
						: null)
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
					.createdByUser(note.getMeta().getCreatedByUser().getEmail())
					.createdAt(note.getMeta().getCreatedAt())
					.modifiedByUser(note.getMeta().getModifiedByUser() != null
							? note.getMeta().getModifiedByUser().getEmail()
							: null)
					.modifiedAt(note.getMeta().getModifiedAt())
					.build();
		}).toList();
		BuildingTransferDto export = BuildingTransferDto
				.builder()
				.meta(meta)
				.id((Integer) building.getId())
				.name(building.getName())
				.description(building.getDescription())
				.buildingNr(building.getBuildingNr())
				.buildingInsuranceNr(building.getInsuranceNr())
				.plotNr(building.getPlotNr())
				.nationalBuildingId(building.getNationalBuildingId())
				.historicPreservation(building.getHistoricPreservation() != null
						? building.getHistoricPreservation().getId()
						: null)
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
				.buildingPartCatalog(building.getCurrentRating().getPartCatalog() != null
						? building.getCurrentRating().getPartCatalog().getId()
						: null)
				.buildingMaintenanceStrategy(building.getCurrentRating().getMaintenanceStrategy() != null
						? building.getCurrentRating().getMaintenanceStrategy().getId()
						: null)
				.ratingStatus(building.getCurrentRating().getRatingStatus().getId())
				.ratingDate(building.getCurrentRating().getRatingDate())
				.ratingUser(building.getCurrentRating().getRatingUser() != null
						? building.getCurrentRating().getRatingUser().getEmail()
						: null)
				.elements(elements)
				.notes(notes)
				.build();
		return export;
	}

	private String getFileName(ObjBuilding building) {
		return (building.getAccount() != null ? building.getAccount().getName() + " " : "") + building.getName() + ".zwbd";
	}

	private void fillFromDto(ObjBuilding building, BuildingTransferDto dto) {
		try {
			building.getMeta().disableCalc();

			ObjUserFM user = (ObjUserFM) this.requestCtx.getUser();
			OffsetDateTime now = this.requestCtx.getCurrentTime();
			building.setOwner(user);
			building.setName(dto.getName());
			building.setDescription(dto.getDescription());
			building.setBuildingNr(dto.getBuildingNr());
			building.setInsuranceNr(dto.getBuildingInsuranceNr());
			building.setPlotNr(dto.getPlotNr());
			building.setNationalBuildingId(dto.getNationalBuildingId());
			building.setHistoricPreservation(CodeHistoricPreservation.getHistoricPreservation(dto.getHistoricPreservation()));
			building.setStreet(dto.getStreet());
			building.setZip(dto.getZip());
			building.setCity(dto.getCity());
			building.setCountry(CodeCountry.getCountry(dto.getCountry()));
			building.setGeoAddress(dto.getGeoAddress());
			building.setGeoCoordinates(dto.getGeoCoordinates());
			building.setGeoZoom(dto.getGeoZoom());
			building.setCurrency(CodeCurrency.getCurrency(dto.getCurrency()));
			building.setVolume(dto.getVolume());
			building.setAreaGross(dto.getAreaGross());
			building.setAreaNet(dto.getAreaNet());
			building.setNrOfFloorsAboveGround(dto.getNrOfFloorsAboveGround());
			building.setNrOfFloorsBelowGround(dto.getNrOfFloorsBelowGround());
			building.setBuildingType(CodeBuildingType.getBuildingType(dto.getBuildingType()));
			building.setBuildingSubType(CodeBuildingSubType.getBuildingSubType(dto.getBuildingSubType()));
			building.setBuildingYear(dto.getBuildingYear());
			building.setInsuredValue(dto.getInsuredValue());
			building.setInsuredValueYear(dto.getInsuredValueYear());
			building.setNotInsuredValue(dto.getNotInsuredValue());
			building.setNotInsuredValueYear(dto.getNotInsuredValueYear());
			building.setThirdPartyValue(dto.getThirdPartyValue());
			building.setThirdPartyValueYear(dto.getThirdPartyValueYear());
			final ObjBuildingPartRating rating = building.getCurrentRating() != null
					? building.getCurrentRating()
					: building.addRating(user, now);
			rating.setPartCatalog(CodeBuildingPartCatalog.getPartCatalog(dto.getBuildingPartCatalog()));
			rating.setMaintenanceStrategy(
					CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(dto.getBuildingMaintenanceStrategy()));
			rating.setRatingStatus(CodeBuildingRatingStatus.getRatingStatus(dto.getRatingStatus()));
			rating.setRatingDate(dto.getRatingDate());
			rating.setRatingUser(dto.getRatingUser() != null
					? this.userRepo.getByEmail(dto.getRatingUser()).get()
					: null);
			if (dto.getElements() != null) {
				dto.getElements().forEach((dtoElement) -> {
					CodeBuildingPart buildingPart = directory.getEnumeration(CodeBuildingPart.class).getItem(dtoElement.getBuildingPart());
					ObjBuildingPartElementRating element = rating.getElement(buildingPart);
					if (element == null) {
						element = rating.addElement(buildingPart);
					}
					element.setWeight(dtoElement.getWeight());
					element.setCondition(dtoElement.getCondition());
					// element.setRatingYear(dtoElement.getRatingYear());
					element.setStrain(dtoElement.getStrain());
					element.setStrength(dtoElement.getStrength());
					element.setDescription(dtoElement.getDescription());
					element.setConditionDescription(dtoElement.getConditionDescription());
					element.setMeasureDescription(dtoElement.getMeasureDescription());
				});
			}
			if (dto.getNotes() != null) {
				CodeNoteType noteType = CodeNoteType.getNoteType("note");
				Object noteUserId = this.requestCtx.getUser().getId();
				OffsetDateTime noteTimestamp = OffsetDateTime.now();
				dto.getNotes().forEach((dtoNote) -> {
					ObjNote note = building.addNote(noteType);
					note.setSubject(dtoNote.getSubject());
					note.setContent(dtoNote.getContent());
					note.setIsPrivate(dtoNote.getIsPrivate());
					this.noteRepo.store(note, noteUserId, noteTimestamp);
				});
			}

		} finally {
			building.getMeta().enableCalc();
			building.calcAll();
		}
	}

}
