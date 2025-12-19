package io.zeitwert.fm.building.adapter.api.rest;

import io.dddrive.core.ddd.model.RepositoryDirectory;
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
import io.zeitwert.fm.building.model.enums.*;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.oe.model.enums.CodeCountry;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@RestController("buildingFileTransferController")
@RequestMapping("/rest/building/buildings")
public class BuildingImportExportController {

	private static final String AGGREGATE = "zeitwert/building";
	private static final String VERSION = "1.0";
	@Autowired
	RequestContextFM requestCtx;
	@Autowired
	private RepositoryDirectory directory;
	@Autowired
	private ObjBuildingRepository buildingRepo;
	@Autowired
	private ObjUserFMRepository userRepo;
	@Autowired
	private ObjNoteRepository noteRepo;

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
		building.accountId = accountId;
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
		List<BuildingTransferElementRatingDto> elements = building.currentRating.getElementList().stream().map(e -> {
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
		List<NoteTransferDto> notes = building.notes.stream().map(note -> {
			return NoteTransferDto
					.builder()
					.subject(note.subject)
					.content(note.content)
					.isPrivate(note.isPrivate)
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
				.name(building.name)
				.description(building.description)
				.buildingNr(building.buildingNr)
				.buildingInsuranceNr(building.insuranceNr)
				.plotNr(building.plotNr)
				.nationalBuildingId(building.nationalBuildingId)
				.historicPreservation(building.historicPreservation != null
						? building.historicPreservation.getId()
						: null)
				.street(building.street)
				.zip(building.zip)
				.city(building.city)
				.country(building.country != null ? building.country.getId() : null)
				.geoAddress(building.geoAddress)
				.geoCoordinates(building.geoCoordinates)
				.geoZoom(building.geoZoom)
				.currency(building.currency != null ? building.currency.getId() : null)
				.volume(building.volume)
				.areaGross(building.areaGross)
				.areaNet(building.areaNet)
				.nrOfFloorsAboveGround(building.nrOfFloorsAboveGround)
				.nrOfFloorsBelowGround(building.nrOfFloorsBelowGround)
				.buildingType(building.buildingType != null ? building.buildingType.getId() : null)
				.buildingSubType(building.buildingSubType != null ? building.buildingSubType.getId() : null)
				.buildingYear(building.buildingYear)
				.insuredValue(building.insuredValue)
				.insuredValueYear(building.insuredValueYear)
				.notInsuredValue(building.notInsuredValue)
				.notInsuredValueYear(building.notInsuredValueYear)
				.thirdPartyValue(building.thirdPartyValue)
				.thirdPartyValueYear(building.thirdPartyValueYear)
				.buildingPartCatalog(building.currentRating.getPartCatalog() != null
						? building.currentRating.getPartCatalog().getId()
						: null)
				.buildingMaintenanceStrategy(building.currentRating.getMaintenanceStrategy() != null
						? building.currentRating.getMaintenanceStrategy().getId()
						: null)
				.ratingStatus(building.currentRating.getRatingStatus().getId())
				.ratingDate(building.currentRating.getRatingDate())
				.ratingUser(building.currentRating.getRatingUser() != null
						? building.currentRating.getRatingUser().getEmail()
						: null)
				.elements(elements)
				.notes(notes)
				.build();
		return export;
	}

	private String getFileName(ObjBuilding building) {
		return (building.account != null ? building.account.getName() + " " : "") + building.name + ".zwbd";
	}

	private void fillFromDto(ObjBuilding building, BuildingTransferDto dto) {
		try {
			building.getMeta().disableCalc();

			ObjUserFM user = (ObjUserFM) this.requestCtx.getUser();
			OffsetDateTime now = this.requestCtx.getCurrentTime();
			building.setOwner(user);
			building.name = dto.getName();
			building.description = dto.getDescription();
			building.buildingNr = dto.getBuildingNr();
			building.insuranceNr = dto.getBuildingInsuranceNr();
			building.plotNr = dto.getPlotNr();
			building.nationalBuildingId = dto.getNationalBuildingId();
			building.historicPreservation = CodeHistoricPreservation.getHistoricPreservation(dto.getHistoricPreservation());
			building.street = dto.getStreet();
			building.zip = dto.getZip();
			building.city = dto.getCity();
			building.country = CodeCountry.getCountry(dto.getCountry());
			building.geoAddress = dto.getGeoAddress();
			building.geoCoordinates = dto.getGeoCoordinates();
			building.geoZoom = dto.getGeoZoom();
			building.currency = CodeCurrency.getCurrency(dto.getCurrency());
			building.volume = dto.getVolume();
			building.areaGross = dto.getAreaGross();
			building.areaNet = dto.getAreaNet();
			building.nrOfFloorsAboveGround = dto.getNrOfFloorsAboveGround();
			building.nrOfFloorsBelowGround = dto.getNrOfFloorsBelowGround();
			building.buildingType = CodeBuildingType.getBuildingType(dto.getBuildingType());
			building.buildingSubType = CodeBuildingSubType.getBuildingSubType(dto.getBuildingSubType());
			building.buildingYear = dto.getBuildingYear();
			building.insuredValue = dto.getInsuredValue();
			building.insuredValueYear = dto.getInsuredValueYear();
			building.notInsuredValue = dto.getNotInsuredValue();
			building.notInsuredValueYear = dto.getNotInsuredValueYear();
			building.thirdPartyValue = dto.getThirdPartyValue();
			building.thirdPartyValueYear = dto.getThirdPartyValueYear();
			final ObjBuildingPartRating rating = building.currentRating != null
					? building.currentRating
					: building.addRating(user, now);
			rating.partCatalog = CodeBuildingPartCatalog.getPartCatalog(dto.getBuildingPartCatalog());
			rating.maintenanceStrategy = CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(dto.getBuildingMaintenanceStrategy());
			rating.ratingStatus = CodeBuildingRatingStatus.getRatingStatus(dto.getRatingStatus());
			rating.ratingDate = dto.getRatingDate();
			rating.ratingUser = dto.getRatingUser() != null
					? this.userRepo.getByEmail(dto.getRatingUser()).get()
					: null;
			if (dto.getElements() != null) {
				dto.getElements().forEach((dtoElement) -> {
					CodeBuildingPart buildingPart = directory.getEnumeration(CodeBuildingPart.class).getItem(dtoElement.getBuildingPart());
					ObjBuildingPartElementRating element = rating.getElement(buildingPart);
					if (element == null) {
						element = rating.addElement(buildingPart);
					}
					element.weight = dtoElement.getWeight();
					element.condition = dtoElement.getCondition();
					// element.setRatingYear(dtoElement.getRatingYear());
					element.strain = dtoElement.getStrain();
					element.strength = dtoElement.getStrength();
					element.description = dtoElement.getDescription();
					element.conditionDescription = dtoElement.getConditionDescription();
					element.measureDescription = dtoElement.getMeasureDescription();
				});
			}
			if (dto.getNotes() != null) {
				CodeNoteType noteType = CodeNoteType.getNoteType("note");
				Object noteUserId = this.requestCtx.getUser().getId();
				OffsetDateTime noteTimestamp = OffsetDateTime.now();
				dto.getNotes().forEach((dtoNote) -> {
					ObjNote note = building.addNote(noteType, noteUserId);
					note.subject = dtoNote.getSubject();
					note.content = dtoNote.getContent();
					note.isPrivate = dtoNote.getIsPrivate();
					this.noteRepo.store(note, noteUserId, noteTimestamp);
				});
			}

		} finally {
			building.getMeta().enableCalc();
			building.calcAll();
		}
	}

}
