package io.zeitwert.fm.building.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.Property
import io.dddrive.core.validation.model.enums.CodeValidationLevelEnum
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

abstract class ObjBuildingBase(
	repository: ObjBuildingRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew),
	ObjBuilding,
	AggregateWithNotesMixin,
	AggregateWithTasksMixin {

	protected val _name = addBaseProperty("name", String::class.java)
	protected val _description = addBaseProperty("description", String::class.java)
	protected val _buildingNr = addBaseProperty("buildingNr", String::class.java)
	protected val _insuranceNr = addBaseProperty("insuranceNr", String::class.java)
	protected val _plotNr = addBaseProperty("plotNr", String::class.java)
	protected val _nationalBuildingId = addBaseProperty("nationalBuilding", String::class.java)
	protected val _historicPreservation =
		addEnumProperty("historicPreservation", CodeHistoricPreservation::class.java)

	protected val _street = addBaseProperty("street", String::class.java)
	protected val _zip = addBaseProperty("zip", String::class.java)
	protected val _city = addBaseProperty("city", String::class.java)
	protected val _country = addEnumProperty("country", CodeCountry::class.java)

	protected val _geoAddress = addBaseProperty("geoAddress", String::class.java)
	protected val _geoCoordinates = addBaseProperty("geoCoordinates", String::class.java)
	protected val _geoZoom = addBaseProperty("geoZoom", Int::class.java)

	protected val _coverFoto = addReferenceProperty("coverFoto", ObjDocument::class.java)

	protected val _currency = addEnumProperty("currency", CodeCurrency::class.java)

	protected val _volume = addBaseProperty("volume", BigDecimal::class.java)
	protected val _areaGross = addBaseProperty("areaGross", BigDecimal::class.java)
	protected val _areaNet = addBaseProperty("areaNet", BigDecimal::class.java)
	protected val _nrOfFloorsAboveGround = addBaseProperty("nrOfFloorsAboveGround", Int::class.java)
	protected val _nrOfFloorsBelowGround = addBaseProperty("nrOfFloorsBelowGround", Int::class.java)

	protected val _buildingType = addEnumProperty("buildingType", CodeBuildingType::class.java)
	protected val _buildingSubType = addEnumProperty("buildingSubType", CodeBuildingSubType::class.java)
	protected val _buildingYear = addBaseProperty("buildingYear", Int::class.java)

	protected val _insuredValue = addBaseProperty("insuredValue", BigDecimal::class.java)
	protected val _insuredValueYear = addBaseProperty("insuredValueYear", Int::class.java)
	protected val _notInsuredValue = addBaseProperty("notInsuredValue", BigDecimal::class.java)
	protected val _notInsuredValueYear = addBaseProperty("notInsuredValueYear", Int::class.java)
	protected val _thirdPartyValue = addBaseProperty("thirdPartyValue", BigDecimal::class.java)
	protected val _thirdPartyValueYear = addBaseProperty("thirdPartyValueYear", Int::class.java)

	protected val _ratingList = addPartListProperty("ratingList", ObjBuildingPartRating::class.java)

	protected val _contactSet = addReferenceSetProperty("contactSet", ObjContact::class.java)

	override val repository = super.repository as ObjBuildingRepository

	override fun noteRepository() = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = repository.taskRepository

	override fun aggregate(): ObjBuilding = this

	override fun doAfterCreate(
		userId: Any,
		timestamp: OffsetDateTime,
	) {
		super.doAfterCreate(userId, timestamp)
		check(this.id != null) { "id must not be null after create" }
		this.addCoverFoto(userId, timestamp)
	}

	override val account get() = if (accountId != null) repository.accountRepository.get(accountId!!) else null

	override fun doAddPart(
		property: Property<*>,
		partId: Int?,
	): Part<*> {
		if (property === this._ratingList) {
			val partRepo = directory.getPartRepository(ObjBuildingPartRating::class.java)
			return partRepo.create(this, property, partId)
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
			for (i in ratingCount downTo 1) {
				val rating = getRating(i - 1)
				if (rating.ratingStatus == null || rating.ratingStatus != CodeBuildingRatingStatus.DISCARD) {
					return rating
				}
			}
			return null
		}

	override fun getCondition(year: Int): Int? = currentRating?.getCondition(year)

	override fun addRating(
		user: ObjUserFM,
		timestamp: OffsetDateTime,
	): ObjBuildingPartRating {
		val oldRating = currentRating
		require(oldRating == null || oldRating.ratingStatus == CodeBuildingRatingStatus.DONE) { "rating done" }
		val rating = _ratingList.addPart(null)
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
			rating.ratingDate = timestamp?.toLocalDate()
		} finally {
			rating.meta.enableCalc()
			rating.meta.calcAll()
		}
		return rating
	}

	override fun removeRating(ratingId: Int) {
		_ratingList.removePart(ratingId)
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
		_caption.value = name
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

	// override fun doCalcSearch() {
	//     super.doCalcSearch()
	//     addSearchText(getZip())
	//     addSearchText(getBuildingNr())
	//     addSearchText(getInsuranceNr())
	//     addSearchText(getPlotNr())
	//     addSearchText(getNationalBuildingId())
	//     addSearchText(getName())
	//     addSearchText(getStreet())
	//     addSearchText(getCity())
	//     addSearchText(getBuildingType()?.name)
	//     addSearchText(getBuildingSubType()?.name)
	//     addSearchText(getCurrentRating()?.partCatalog?.name)
	//     addSearchText(getDescription())
	// }

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
		_coverFoto.id = coverFoto.id
	}

}
