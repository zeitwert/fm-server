package io.zeitwert.fm.building.model.impl

import io.dddrive.ddd.model.Part
import io.dddrive.property.delegate.baseProperty
import io.dddrive.property.delegate.enumProperty
import io.dddrive.property.delegate.partListProperty
import io.dddrive.property.delegate.referenceIdProperty
import io.dddrive.property.delegate.referenceProperty
import io.dddrive.property.delegate.referenceSetProperty
import io.dddrive.property.model.PartListProperty
import io.dddrive.property.model.Property
import io.dddrive.property.model.ReferenceSetProperty
import io.dddrive.validation.model.enums.CodeValidationLevelEnum
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
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjUserFM
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
	override var name: String? by baseProperty(this, "name")
	override var description: String? by baseProperty(this, "description")
	override var buildingNr: String? by baseProperty(this, "buildingNr")
	override var insuranceNr: String? by baseProperty(this, "insuranceNr")
	override var plotNr: String? by baseProperty(this, "plotNr")
	override var nationalBuildingId: String? by baseProperty(this, "nationalBuildingId")
	override var street: String? by baseProperty(this, "street")
	override var zip: String? by baseProperty(this, "zip")
	override var city: String? by baseProperty(this, "city")
	override var geoAddress: String? by baseProperty(this, "geoAddress")
	override var geoCoordinates: String? by baseProperty(this, "geoCoordinates")
	override var geoZoom: Int? by baseProperty(this, "geoZoom")
	override var volume: BigDecimal? by baseProperty(this, "volume")
	override var areaGross: BigDecimal? by baseProperty(this, "areaGross")
	override var areaNet: BigDecimal? by baseProperty(this, "areaNet")
	override var nrOfFloorsAboveGround: Int? by baseProperty(this, "nrOfFloorsAboveGround")
	override var nrOfFloorsBelowGround: Int? by baseProperty(this, "nrOfFloorsBelowGround")
	override var buildingYear: Int? by baseProperty(this, "buildingYear")
	override var insuredValue: BigDecimal? by baseProperty(this, "insuredValue")
	override var insuredValueYear: Int? by baseProperty(this, "insuredValueYear")
	override var notInsuredValue: BigDecimal? by baseProperty(this, "notInsuredValue")
	override var notInsuredValueYear: Int? by baseProperty(this, "notInsuredValueYear")
	override var thirdPartyValue: BigDecimal? by baseProperty(this, "thirdPartyValue")
	override var thirdPartyValueYear: Int? by baseProperty(this, "thirdPartyValueYear")

	// Enum properties
	override var historicPreservation: CodeHistoricPreservation? by enumProperty(this, "historicPreservation")
	override var country: CodeCountry? by enumProperty(this, "country")
	override var currency: CodeCurrency? by enumProperty(this, "currency")
	override var buildingType: CodeBuildingType? by enumProperty(this, "buildingType")
	override var buildingSubType: CodeBuildingSubType? by enumProperty(this, "buildingSubType")

	// Reference properties (coverFoto)
	override var coverFotoId: Any? by referenceIdProperty<ObjDocument>(this, "coverFotoId")
	override var coverFoto: ObjDocument? by referenceProperty(this, "coverFoto")

	// Part list property
	override val ratingList: PartListProperty<ObjBuildingPartRating> by partListProperty(this, "ratingList")

	// Reference set property
	override val contactSet: ReferenceSetProperty<ObjContact> by referenceSetProperty(this, "contactSet")

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = repository.taskRepository

	override fun aggregate(): ObjBuilding = this

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		this.addCoverFoto(userId, timestamp)
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

	override fun doBeforeStore(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doBeforeStore(userId, timestamp)
		if (coverFotoId == null) {
			addCoverFoto(userId, timestamp)
		}
	}

	override val inflationRate: Double
		get() {
			var inflationRate = account?.inflationRate
			if (inflationRate == null) {
				inflationRate = (tenant as? ObjTenantFM)?.inflationRate
			}
			return inflationRate?.toDouble() ?: 0.0
		}

	override val discountRate: Double
		get() {
			var discountRate = account?.discountRate
			if (discountRate == null) {
				discountRate = (tenant as? ObjTenantFM)?.discountRate
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

	override val currentRating: ObjBuildingPartRating?
		get() {
			for (i in ratingList.size downTo 1) {
				val rating = ratingList.get(i - 1)
				if (rating.ratingStatus == null || rating.ratingStatus != CodeBuildingRatingStatus.DISCARD) {
					return rating
				}
			}
			return null
		}

	override val currentRatingForView: ObjBuildingPartRating?
		get() = currentRating

	override fun getCondition(year: Int): Int? = currentRating?.getCondition(year)

	override fun addRating(
		user: ObjUserFM,
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
			rating.ratingUser = user
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

	private fun addCoverFoto(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		val documentRepo = repository.documentRepository
		val coverFoto = documentRepo.create(tenantId, userId, timestamp)
		coverFoto.name = "CoverFoto"
		coverFoto.contentKind = CodeContentKind.FOTO
		coverFoto.documentKind = CodeDocumentKind.STANDALONE
		coverFoto.documentCategory = CodeDocumentCategory.FOTO
		documentRepo.store(coverFoto, userId, timestamp)
		this.coverFotoId = coverFoto.id
	}

}
