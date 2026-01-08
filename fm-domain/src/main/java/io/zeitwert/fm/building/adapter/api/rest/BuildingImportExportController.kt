package io.zeitwert.fm.building.adapter.api.rest

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferDto
import io.zeitwert.fm.building.adapter.api.rest.dto.BuildingTransferElementRatingDto
import io.zeitwert.fm.building.adapter.api.rest.dto.NoteTransferDto
import io.zeitwert.fm.building.adapter.api.rest.dto.TransferMetaDto
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog.Enumeration.getPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus.Enumeration.getRatingStatus
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType.Enumeration.getBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType.Enumeration.getBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation.Enumeration.getHistoricPreservation
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType.Enumeration.getNoteType
import io.zeitwert.fm.oe.model.ObjUserRepository
import io.zeitwert.fm.oe.model.enums.CodeCountry.Enumeration.getCountry
import jakarta.servlet.ServletException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.util.function.Consumer

@RestController("buildingFileTransferController")
@RequestMapping("/rest/building/buildings")
class BuildingImportExportController {

	@Autowired
	lateinit var sessionCtx: SessionContext

	@Autowired
	lateinit var directory: RepositoryDirectory

	@Autowired
	lateinit var buildingRepo: ObjBuildingRepository

	@Autowired
	lateinit var userRepo: ObjUserRepository

	@Autowired
	lateinit var noteRepo: ObjNoteRepository

	@GetMapping("/{id}")
	@Throws(ServletException::class, IOException::class)
	fun exportBuilding(
		@PathVariable("id") id: Int,
	): ResponseEntity<BuildingTransferDto?> {
		val building = buildingRepo.get(id)
		val export = getTransferDto(building)
		val fileName = getFileName(building)
		val contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build()
		val headers = HttpHeaders()
		headers.contentDisposition = contentDisposition
		return ResponseEntity.ok().headers(headers).body<BuildingTransferDto?>(export)
	}

	@PostMapping
	@Throws(ServletException::class, IOException::class)
	fun importBuilding(
		@RequestBody dto: BuildingTransferDto,
	): ResponseEntity<BuildingTransferDto?> {
		val accountId = sessionCtx.accountId
		if (accountId == null) {
			return ResponseEntity.badRequest().build<BuildingTransferDto?>()
		} else if (AGGREGATE != dto.meta?.aggregate) {
			return ResponseEntity.unprocessableEntity().build<BuildingTransferDto?>()
		} else if (VERSION != dto.meta.version) {
			return ResponseEntity.unprocessableEntity().build<BuildingTransferDto?>()
		}
		val building = buildingRepo.create()
		building.accountId = accountId
		fillFromDto(building, dto)
		buildingRepo.store(building)
		val export = getTransferDto(building)
		return ResponseEntity.ok().body<BuildingTransferDto?>(export)
	}

	private fun getTransferDto(building: ObjBuilding): BuildingTransferDto {
		val meta = TransferMetaDto(
			aggregate = AGGREGATE,
			version = VERSION,
			createdByUser = userRepo.get(building.meta.createdByUserId).email,
			createdAt = building.meta.createdAt,
			modifiedByUser = if (building.meta.modifiedByUserId != null) userRepo.get(building.meta.modifiedByUserId!!).email else null,
			modifiedAt = building.meta.modifiedAt,
		)
		val elements = building.currentRating!!
			.elementList
			.stream()
			.map { e: ObjBuildingPartElementRating ->
				BuildingTransferElementRatingDto(
					buildingPart = e.buildingPart!!.id,
					weight = e.weight,
					condition = e.condition,
					ratingYear = e.ratingYear,
					strain = e.strain,
					strength = e.strength,
					description = e.description,
					conditionDescription = e.conditionDescription,
					measureDescription = e.measureDescription,
				)
			}.toList()
		val notes = building.notes
			.stream()
			.map { noteId ->
				val note = noteRepo.load(noteId)
				NoteTransferDto(
					subject = note.subject,
					content = note.content,
					isPrivate = note.isPrivate,
					createdByUser = userRepo.get(note.meta.createdByUserId).email,
					createdAt = note.meta.createdAt,
					modifiedByUser = if (note.meta.modifiedByUserId != null) userRepo.get(note.meta.modifiedByUserId!!).email else null,
					modifiedAt = note.meta.modifiedAt,
				)
			}.toList()
		val currentRating = building.currentRating!!
		val export = BuildingTransferDto(
			meta = meta,
			id = building.id as Int,
			name = building.name,
			description = building.description,
			buildingNr = building.buildingNr,
			buildingInsuranceNr = building.insuranceNr,
			plotNr = building.plotNr,
			nationalBuildingId = building.nationalBuildingId,
			historicPreservation = building.historicPreservation?.id,
			street = building.street,
			zip = building.zip,
			city = building.city,
			country = building.country?.id,
			geoAddress = building.geoAddress,
			geoCoordinates = building.geoCoordinates,
			geoZoom = building.geoZoom,
			currency = building.currency?.id,
			volume = building.volume,
			areaGross = building.areaGross,
			areaNet = building.areaNet,
			nrOfFloorsAboveGround = building.nrOfFloorsAboveGround,
			nrOfFloorsBelowGround = building.nrOfFloorsBelowGround,
			buildingType = building.buildingType?.id,
			buildingSubType = building.buildingSubType?.id,
			buildingYear = building.buildingYear,
			insuredValue = building.insuredValue,
			insuredValueYear = building.insuredValueYear,
			notInsuredValue = building.notInsuredValue,
			notInsuredValueYear = building.notInsuredValueYear,
			thirdPartyValue = building.thirdPartyValue,
			thirdPartyValueYear = building.thirdPartyValueYear,
			buildingPartCatalog = currentRating.partCatalog?.id,
			buildingMaintenanceStrategy = currentRating.maintenanceStrategy?.id,
			ratingStatus = currentRating.ratingStatus?.id,
			ratingDate = currentRating.ratingDate,
			ratingUser = currentRating.ratingUser?.email,
			elements = elements,
			notes = notes,
		)
		return export
	}

	private fun getFileName(building: ObjBuilding): String = (if (building.account != null) building.account!!.name + " " else "") + building.name + ".zwbd"

	private fun fillFromDto(
		building: ObjBuilding,
		dto: BuildingTransferDto,
	) {
		try {
			val userId = sessionCtx.userId
			val now = sessionCtx.currentTime

			building.meta.disableCalc()

			building.ownerId = userId
			building.name = dto.name
			building.description = dto.description
			building.buildingNr = dto.buildingNr
			building.insuranceNr = dto.buildingInsuranceNr
			building.plotNr = dto.plotNr
			building.nationalBuildingId = dto.nationalBuildingId
			building.historicPreservation = getHistoricPreservation(dto.historicPreservation)
			building.street = dto.street
			building.zip = dto.zip
			building.city = dto.city
			building.country = getCountry(dto.country)
			building.geoAddress = dto.geoAddress
			building.geoCoordinates = dto.geoCoordinates
			building.geoZoom = dto.geoZoom
			building.currency = CodeCurrency.getCurrency(dto.currency)
			building.volume = dto.volume
			building.areaGross = dto.areaGross
			building.areaNet = dto.areaNet
			building.nrOfFloorsAboveGround = dto.nrOfFloorsAboveGround
			building.nrOfFloorsBelowGround = dto.nrOfFloorsBelowGround
			building.buildingType = getBuildingType(dto.buildingType)
			building.buildingSubType = getBuildingSubType(dto.buildingSubType)
			building.buildingYear = dto.buildingYear
			building.insuredValue = dto.insuredValue
			building.insuredValueYear = dto.insuredValueYear
			building.notInsuredValue = dto.notInsuredValue
			building.notInsuredValueYear = dto.notInsuredValueYear
			building.thirdPartyValue = dto.thirdPartyValue
			building.thirdPartyValueYear = dto.thirdPartyValueYear
			val rating: ObjBuildingPartRating = (
				if (building.currentRating != null) {
					building.currentRating
				} else {
					building.addRating(userId, now)
				}
			)!!
			rating.partCatalog = getPartCatalog(dto.buildingPartCatalog)
			rating.maintenanceStrategy =
				CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(dto.buildingMaintenanceStrategy)
			rating.ratingStatus = getRatingStatus(dto.ratingStatus)
			rating.ratingDate = dto.ratingDate
			rating.ratingUser = if (dto.ratingUser != null) {
				userRepo.getByEmail(dto.ratingUser).get()
			} else {
				null
			}
			if (dto.elements != null) {
				dto.elements.forEach(
					Consumer { dtoElement: BuildingTransferElementRatingDto? ->
						val buildingPart = directory!!
							.getEnumeration<CodeBuildingPart>(
								CodeBuildingPart::class.java,
							).getItem(dtoElement!!.buildingPart)
						var element = rating.getElement(buildingPart)
						if (element == null) {
							element = rating.addElement(buildingPart)
						}
						element.weight = dtoElement.weight
						element.condition = dtoElement.condition
						// element.setRatingYear(dtoElement.getRatingYear());
						element.strain = dtoElement.strain
						element.strength = dtoElement.strength
						element.description = dtoElement.description
						element.conditionDescription = dtoElement.conditionDescription
						element.measureDescription = dtoElement.measureDescription
					},
				)
			}
			if (dto.notes != null) {
				val noteType = getNoteType("note")
				val noteUserId = userId
				dto.notes.forEach(
					Consumer { dtoNote: NoteTransferDto? ->
						val note = building.addNote(noteType!!, noteUserId)
						note.subject = dtoNote!!.subject
						note.content = dtoNote.content
						note.isPrivate = dtoNote.isPrivate
						noteRepo.store(note)
					},
				)
			}
		} finally {
			building.meta.enableCalc()
			building.meta.calcAll()
		}
	}

	companion object {

		private const val AGGREGATE = "zeitwert/building"
		private const val VERSION = "1.0"
	}

}
