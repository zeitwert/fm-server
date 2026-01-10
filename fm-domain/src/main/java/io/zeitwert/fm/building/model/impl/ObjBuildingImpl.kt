package io.zeitwert.fm.building.model.impl

import dddrive.app.ddd.model.SessionContext
import dddrive.app.ddd.model.enums.CodeValidationLevelEnum
import dddrive.ddd.model.Part
import dddrive.ddd.property.delegate.baseProperty
import dddrive.ddd.property.delegate.enumProperty
import dddrive.ddd.property.delegate.partListProperty
import dddrive.ddd.property.delegate.partReferenceProperty
import dddrive.ddd.property.delegate.referenceIdProperty
import dddrive.ddd.property.delegate.referenceProperty
import dddrive.ddd.property.delegate.referenceSetProperty
import dddrive.ddd.property.model.Property
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPriceIndex
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.dms.model.enums.CodeContentKind
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin
import java.math.BigDecimal
import java.time.OffsetDateTime

class ObjBuildingImpl(
	override val repository: ObjBuildingRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjBuilding,
	AggregateWithNotesMixin,
	AggregateWithTasksMixin {

	// Base properties
	override var name by baseProperty<String>("name")
	override var description by baseProperty<String>("description")
	override var buildingNr by baseProperty<String>("buildingNr")
	override var insuranceNr by baseProperty<String>("insuranceNr")
	override var plotNr by baseProperty<String>("plotNr")
	override var nationalBuildingId by baseProperty<String>("nationalBuildingId")
	override var street by baseProperty<String>("street")
	override var zip by baseProperty<String>("zip")
	override var city by baseProperty<String>("city")
	override var geoAddress by baseProperty<String>("geoAddress")
	override var geoCoordinates by baseProperty<String>("geoCoordinates")
	override var geoZoom by baseProperty<Int>("geoZoom")
	override var volume by baseProperty<BigDecimal>("volume")
	override var areaGross by baseProperty<BigDecimal>("areaGross")
	override var areaNet by baseProperty<BigDecimal>("areaNet")
	override var nrOfFloorsAboveGround by baseProperty<Int>("nrOfFloorsAboveGround")
	override var nrOfFloorsBelowGround by baseProperty<Int>("nrOfFloorsBelowGround")
	override var buildingYear by baseProperty<Int>("buildingYear")
	override var insuredValue by baseProperty<BigDecimal>("insuredValue")
	override var insuredValueYear by baseProperty<Int>("insuredValueYear")
	override var notInsuredValue by baseProperty<BigDecimal>("notInsuredValue")
	override var notInsuredValueYear by baseProperty<Int>("notInsuredValueYear")
	override var thirdPartyValue by baseProperty<BigDecimal>("thirdPartyValue")
	override var thirdPartyValueYear by baseProperty<Int>("thirdPartyValueYear")

	// Enum properties
	override var historicPreservation by enumProperty<CodeHistoricPreservation>("historicPreservation")
	override var country by enumProperty<CodeCountry>("country")
	override var currency by enumProperty<CodeCurrency>("currency")
	override var buildingType by enumProperty<CodeBuildingType>("buildingType")
	override var buildingSubType by enumProperty<CodeBuildingSubType>("buildingSubType")

	// Reference properties (coverFoto)
	override var coverFotoId by referenceIdProperty<ObjDocument>("coverFoto")
	override var coverFoto by referenceProperty<ObjDocument>("coverFoto")

	// Part list property
	override val ratingList = partListProperty<ObjBuilding, ObjBuildingPartRating>("ratingList")

	// Reference set property
	override val contactSet = referenceSetProperty<ObjContact>("contactSet")

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = repository.taskRepository

	val tenantRepo = directory.getRepository(ObjTenant::class.java)

	override fun aggregate(): ObjBuilding = this

	override fun doAfterCreate(sessionContext: SessionContext) {
		super.doAfterCreate(sessionContext)
		this.addCoverFoto()
	}

	override val account get() = if (accountId != null) repository.accountRepository.get(accountId!!) else null

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this.ratingList) {
			return directory.getPartRepository(ObjBuildingPartRating::class.java).create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doBeforeStore(sessionContext: SessionContext) {
		super.doBeforeStore(sessionContext)
		if (coverFotoId == null) {
			addCoverFoto()
		}
	}

	override val inflationRate: Double
		get() {
			var inflationRate = account?.inflationRate
			if (inflationRate == null) {
				inflationRate = tenantRepo.get(tenantId).inflationRate
			}
			return inflationRate?.toDouble() ?: 0.0
		}

	override val discountRate: Double
		get() {
			var discountRate = account?.discountRate
			if (discountRate == null) {
				discountRate = tenantRepo.get(tenantId).discountRate
			}
			return discountRate?.toDouble() ?: 0.0
		}

	override fun getBuildingValue(year: Int): Double {
		val insuredValueYear = insuredValueYear
		val insuredValue = insuredValue
		if (insuredValueYear != null && insuredValue != null) {
			return CodeBuildingPriceIndex.CH_ZRH.priceAt(
				insuredValueYear,
				1000.0 * insuredValue.toDouble(),
				year,
				inflationRate,
			)
		}
		return 0.0
	}

	// Computed property - calculator returns the part ID, value is derived via getPart()
	override var currentRating by partReferenceProperty<ObjBuilding, ObjBuildingPartRating>("currentRating") { _ ->
		for (i in ratingList.size downTo 1) {
			val rating = ratingList.get(i - 1)
			if (rating.ratingStatus == null || rating.ratingStatus != CodeBuildingRatingStatus.DISCARD) {
				return@partReferenceProperty rating.id
			}
		}
		null
	}

	override val currentRatingForView: ObjBuildingPartRating?
		get() = currentRating

	override fun getCondition(year: Int): Int? = currentRating?.getCondition(year)

	override fun addRating(
		userId: Any,
		timestamp: OffsetDateTime,
	): ObjBuildingPartRating {
		val oldRating = currentRating
		require(oldRating == null || oldRating.ratingStatus == CodeBuildingRatingStatus.DONE) { "rating done" }
		val rating = ratingList.add(null)
		try {
			rating.meta.disableCalc()
			rating.ratingStatus = CodeBuildingRatingStatus.OPEN
			if (oldRating != null) {
				rating.partCatalog = oldRating.partCatalog
				rating.maintenanceStrategy = oldRating.maintenanceStrategy
			} else {
				rating.maintenanceStrategy = CodeBuildingMaintenanceStrategy.N
			}
			rating.ratingUserId = userId
			rating.ratingDate = timestamp.toLocalDate()
		} finally {
			rating.meta.enableCalc()
			rating.meta.calcAll()
		}
		return rating
	}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		currentRating?.meta?.calcAll()
		validateElements()
	}

	override fun doCalcVolatile() {
		super.doCalcVolatile()
		calcCaption()
		currentRating?.meta?.calcVolatile()
		validateElements()
	}

	private fun calcCaption() {
		setCaption(name)
	}

	@Suppress("LongMethod")
	private fun validateElements() {
		if (coverFoto == null || coverFoto?.contentType == null) {
			addValidation(CodeValidationLevelEnum.WARNING, "Für den Druck muss ein Coverfoto hochgeladen werden")
		}
		if (geoCoordinates.isNullOrEmpty()) {
			addValidation(CodeValidationLevelEnum.WARNING, "Koordinaten der Immobilie fehlen")
		}
		if (insuredValue == null || insuredValue == BigDecimal.ZERO) {
			addValidation(CodeValidationLevelEnum.ERROR, "Versicherungswert muss erfasst werden")
		}
		if (insuredValueYear == null) {
			addValidation(CodeValidationLevelEnum.ERROR, "Jahr der Bestimmung des Versicherungswerts muss erfasst werden")
		}
		val currentRating = currentRating
		if (currentRating == null) {
			addValidation(CodeValidationLevelEnum.ERROR, "Es fehlt eine Zustandsbewertung")
		} else {
			if (currentRating.ratingDate == null) {
				addValidation(CodeValidationLevelEnum.ERROR, "Datum der Zustandsbewertung muss erfasst werden")
			}
			if (currentRating.elementWeights != 100) {
				addValidation(
					CodeValidationLevelEnum.ERROR,
					"Summe der Bauteilanteile muss 100% sein (ist ${currentRating.elementWeights}%)",
				)
			}
			for (element in currentRating.elementList) {
				if (element.weight != null && element.weight != 0) {
					if (element.condition == null || element.condition == 0) {
						addValidation(
							CodeValidationLevelEnum.ERROR,
							"Zustand für Element [${element.buildingPart?.name}] muss erfasst werden",
						)
					} else if (element.ratingYear == null || element.ratingYear!! < 1800) {
						addValidation(
							CodeValidationLevelEnum.ERROR,
							"Jahr der Zustandsbewertung für Element [${element.buildingPart?.name}] muss erfasst werden",
						)
					}
				}
			}
		}
	}

	private fun addCoverFoto() {
		val documentRepo = repository.documentRepository
		val coverFoto = documentRepo.create()
		coverFoto.name = "CoverFoto"
		coverFoto.contentKind = CodeContentKind.FOTO
		coverFoto.documentKind = CodeDocumentKind.STANDALONE
		coverFoto.documentCategory = CodeDocumentCategory.FOTO
		documentRepo.store(coverFoto)
		this.coverFotoId = coverFoto.id
	}

}
