package io.zeitwert.fm.building.model.base

import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.*
import io.dddrive.core.validation.model.enums.CodeValidationLevelEnum
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.enums.*
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
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin
import java.math.BigDecimal
import java.time.OffsetDateTime

abstract class ObjBuildingBase(
	repository: ObjBuildingRepository
) : FMObjBase(repository), ObjBuilding, AggregateWithNotesMixin, AggregateWithTasksMixin {

	private val _name: BaseProperty<String> = this.addBaseProperty("name", String::class.java)
	private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
	private val _buildingNr: BaseProperty<String> = this.addBaseProperty("buildingNr", String::class.java)
	private val _insuranceNr: BaseProperty<String> = this.addBaseProperty("insuranceNr", String::class.java)
	private val _plotNr: BaseProperty<String> = this.addBaseProperty("plotNr", String::class.java)
	private val _nationalBuildingId: BaseProperty<String> = this.addBaseProperty("nationalBuilding", String::class.java)
	private val _historicPreservation: EnumProperty<CodeHistoricPreservation> =
		this.addEnumProperty("historicPreservation", CodeHistoricPreservation::class.java)

	private val _street: BaseProperty<String> = this.addBaseProperty("street", String::class.java)
	private val _zip: BaseProperty<String> = this.addBaseProperty("zip", String::class.java)
	private val _city: BaseProperty<String> = this.addBaseProperty("city", String::class.java)
	private val _country: EnumProperty<CodeCountry> = this.addEnumProperty("country", CodeCountry::class.java)

	private val _geoAddress: BaseProperty<String> = this.addBaseProperty("geoAddress", String::class.java)
	private val _geoCoordinates: BaseProperty<String> = this.addBaseProperty("geoCoordinates", String::class.java)
	private val _geoZoom: BaseProperty<Int> = this.addBaseProperty("geoZoom", Int::class.java)

	private val _coverFoto: ReferenceProperty<ObjDocument> =
		this.addReferenceProperty("coverFoto", ObjDocument::class.java)

	private val _currency: EnumProperty<CodeCurrency> = this.addEnumProperty("currency", CodeCurrency::class.java)

	private val _volume: BaseProperty<BigDecimal> = this.addBaseProperty("volume", BigDecimal::class.java)
	private val _areaGross: BaseProperty<BigDecimal> = this.addBaseProperty("areaGross", BigDecimal::class.java)
	private val _areaNet: BaseProperty<BigDecimal> = this.addBaseProperty("areaNet", BigDecimal::class.java)
	private val _nrOfFloorsAboveGround: BaseProperty<Int> = this.addBaseProperty("nrOfFloorsAboveGround", Int::class.java)
	private val _nrOfFloorsBelowGround: BaseProperty<Int> = this.addBaseProperty("nrOfFloorsBelowGround", Int::class.java)

	private val _buildingType: EnumProperty<CodeBuildingType> =
		this.addEnumProperty("buildingType", CodeBuildingType::class.java)
	private val _buildingSubType: EnumProperty<CodeBuildingSubType> =
		this.addEnumProperty("buildingSubType", CodeBuildingSubType::class.java)
	private val _buildingYear: BaseProperty<Int> = this.addBaseProperty("buildingYear", Int::class.java)

	private val _insuredValue: BaseProperty<BigDecimal> = this.addBaseProperty("insuredValue", BigDecimal::class.java)
	private val _insuredValueYear: BaseProperty<Int> = this.addBaseProperty("insuredValueYear", Int::class.java)
	private val _notInsuredValue: BaseProperty<BigDecimal> =
		this.addBaseProperty("notInsuredValue", BigDecimal::class.java)
	private val _notInsuredValueYear: BaseProperty<Int> = this.addBaseProperty("notInsuredValueYear", Int::class.java)
	private val _thirdPartyValue: BaseProperty<BigDecimal> =
		this.addBaseProperty("thirdPartyValue", BigDecimal::class.java)
	private val _thirdPartyValueYear: BaseProperty<Int> = this.addBaseProperty("thirdPartyValueYear", Int::class.java)

	private val _ratingList: PartListProperty<ObjBuildingPartRating> =
		this.addPartListProperty("ratingList", ObjBuildingPartRating::class.java)

	private val _contactSet: ReferenceSetProperty<ObjContact> =
		this.addReferenceSetProperty("contactSet", ObjContact::class.java)

	override fun getRepository(): ObjBuildingRepository = super.getRepository() as ObjBuildingRepository

	override fun noteRepository(): ObjNoteRepository = directory.getRepository(ObjNote::class.java) as ObjNoteRepository

	override fun taskRepository() = repository.taskRepository

	override fun aggregate(): ObjBuilding = this

	override fun doAfterCreate(userId: Any?, timestamp: OffsetDateTime?) {
		super.doAfterCreate(userId, timestamp)
		check(this.id != null) { "id must not be null after create" }
		this.addCoverFoto(userId, timestamp)
	}

	override fun getAccount(): ObjAccount? {
		return repository.accountRepository.get(accountId)
	}

	override fun doAddPart(property: Property<*>, partId: Int?): Part<*>? {
		if (property === this._ratingList) {
			val partRepo = directory.getPartRepository(ObjBuildingPartRating::class.java)
			return partRepo.create(this, property, partId)
		}
		return super.doAddPart(property, partId)
	}

	override fun doBeforeStore(userId: Any?, timestamp: OffsetDateTime?) {
		super.doBeforeStore(userId, timestamp)
		if (getCoverFotoId() == null) {
			addCoverFoto(userId, timestamp)
		}
	}

	override fun getInflationRate(): Double {
		var inflationRate = account?.inflationRate
		if (inflationRate == null) {
			inflationRate = (tenant as? ObjTenantFM)?.inflationRate
		}
		return inflationRate?.toDouble() ?: 0.0
	}

	override fun getDiscountRate(): Double {
		var discountRate = account?.discountRate
		if (discountRate == null) {
			discountRate = (tenant as? ObjTenantFM)?.discountRate
		}
		return discountRate?.toDouble() ?: 0.0
	}

	override fun getBuildingValue(year: Int): Double {
		val insuredValueYear = getInsuredValueYear()
		val insuredValue = getInsuredValue()
		if (insuredValueYear != null && insuredValue != null) {
			return CodeBuildingPriceIndex.CH_ZRH.priceAt(
				insuredValueYear,
				1000.0 * insuredValue.toDouble(),
				year,
				getInflationRate()
			)
		}
		return 0.0
	}

	override fun getCurrentRating(): ObjBuildingPartRating? {
		for (i in getRatingCount() downTo 1) {
			val rating = getRating(i - 1)
			if (rating?.ratingStatus == null || rating.ratingStatus != CodeBuildingRatingStatus.DISCARD) {
				return rating
			}
		}
		return null
	}

	override fun getCondition(year: Int): Int? {
		return getCurrentRating()?.getCondition(year)
	}

	override fun addRating(user: ObjUserFM?, timestamp: OffsetDateTime?): ObjBuildingPartRating {
		val oldRating = getCurrentRating()
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
			rating.calcAll()
		}
		return rating
	}

	override fun removeRating(ratingId: Int?) {
		_ratingList.removePart(ratingId)
	}

	override fun doCalcAll() {
		super.doCalcAll()
		calcCaption()
		getCurrentRating()?.calcAll()
		validateElements()
	}

	override fun doCalcVolatile() {
		super.doCalcVolatile()
		calcCaption()
		getCurrentRating()?.calcVolatile()
		validateElements()
	}

	private fun calcCaption() {
		caption.value = name
	}

	@Suppress("LongMethod")
	private fun validateElements() {
		if (getCoverFoto() == null || getCoverFoto()?.contentType == null) {
			addValidation(CodeValidationLevelEnum.WARNING, "Für den Druck muss ein Coverfoto hochgeladen werden")
		}
		if (getGeoCoordinates().isNullOrEmpty()) {
			addValidation(CodeValidationLevelEnum.WARNING, "Koordinaten der Immobilie fehlen")
		}
		if (getInsuredValue() == null || getInsuredValue() == BigDecimal.ZERO) {
			addValidation(CodeValidationLevelEnum.ERROR, "Versicherungswert muss erfasst werden")
		}
		if (getInsuredValueYear() == null) {
			addValidation(CodeValidationLevelEnum.ERROR, "Jahr der Bestimmung des Versicherungswerts muss erfasst werden")
		}
		val currentRating = getCurrentRating()
		if (currentRating == null) {
			addValidation(CodeValidationLevelEnum.ERROR, "Es fehlt eine Zustandsbewertung")
		} else {
			if (currentRating.ratingDate == null) {
				addValidation(CodeValidationLevelEnum.ERROR, "Datum der Zustandsbewertung muss erfasst werden")
			}
			if (currentRating.elementWeights != 100) {
				addValidation(
					CodeValidationLevelEnum.ERROR,
					"Summe der Bauteilanteile muss 100% sein (ist ${currentRating.elementWeights}%)"
				)
			}
			for (element in currentRating.elementList) {
				if (element.weight != null && element.weight != 0) {
					if (element.condition == null || element.condition == 0) {
						addValidation(
							CodeValidationLevelEnum.ERROR,
							"Zustand für Element [${element.buildingPart?.name}] muss erfasst werden"
						)
					} else if (element.ratingYear == null || element.ratingYear!! < 1800) {
						addValidation(
							CodeValidationLevelEnum.ERROR,
							"Jahr der Zustandsbewertung für Element [${element.buildingPart?.name}] muss erfasst werden"
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

	private fun addCoverFoto(userId: Any?, timestamp: OffsetDateTime?) {
		val documentRepo = repository.documentRepository
		val coverFoto = documentRepo.create(tenantId, userId, timestamp)
		coverFoto.name = "CoverFoto"
		coverFoto.contentKind = CodeContentKind.FOTO
		coverFoto.documentKind = CodeDocumentKind.STANDALONE
		coverFoto.documentCategory = CodeDocumentCategory.FOTO
		documentRepo.store(coverFoto, userId, timestamp)
		_coverFoto.id = coverFoto.id
	}

	override fun getName(): String? = _name.value

	override fun setName(name: String?) {
		_name.value = name
	}

	override fun getDescription(): String? = _description.value

	override fun setDescription(description: String?) {
		_description.value = description
	}

	override fun getBuildingNr(): String? = _buildingNr.value

	override fun setBuildingNr(buildingNr: String?) {
		_buildingNr.value = buildingNr
	}

	override fun getInsuranceNr(): String? = _insuranceNr.value

	override fun setInsuranceNr(insuranceNr: String?) {
		_insuranceNr.value = insuranceNr
	}

	override fun getPlotNr(): String? = _plotNr.value

	override fun setPlotNr(plotNr: String?) {
		_plotNr.value = plotNr
	}

	override fun getNationalBuildingId(): String? = _nationalBuildingId.value

	override fun setNationalBuildingId(nationalBuildingId: String?) {
		_nationalBuildingId.value = nationalBuildingId
	}

	override fun getHistoricPreservation(): CodeHistoricPreservation? = _historicPreservation.value

	override fun setHistoricPreservation(historicPreservation: CodeHistoricPreservation?) {
		_historicPreservation.value = historicPreservation
	}

	override fun getStreet(): String? = _street.value

	override fun setStreet(street: String?) {
		_street.value = street
	}

	override fun getZip(): String? = _zip.value

	override fun setZip(zip: String?) {
		_zip.value = zip
	}

	override fun getCity(): String? = _city.value

	override fun setCity(city: String?) {
		_city.value = city
	}

	override fun getCountry(): CodeCountry? = _country.value

	override fun setCountry(country: CodeCountry?) {
		_country.value = country
	}

	override fun getGeoAddress(): String? = _geoAddress.value

	override fun setGeoAddress(geoAddress: String?) {
		_geoAddress.value = geoAddress
	}

	override fun getGeoCoordinates(): String? = _geoCoordinates.value

	override fun setGeoCoordinates(geoCoordinates: String?) {
		_geoCoordinates.value = geoCoordinates
	}

	override fun getGeoZoom(): Int? = _geoZoom.value

	override fun setGeoZoom(geoZoom: Int?) {
		_geoZoom.value = geoZoom
	}

	override fun getCoverFotoId(): Int? = _coverFoto.id as? Int

	override fun getCoverFoto(): ObjDocument? = _coverFoto.value

	override fun getCurrency(): CodeCurrency? = _currency.value

	override fun setCurrency(currency: CodeCurrency?) {
		_currency.value = currency
	}

	override fun getVolume(): BigDecimal? = _volume.value

	override fun setVolume(volume: BigDecimal?) {
		_volume.value = volume
	}

	override fun getAreaGross(): BigDecimal? = _areaGross.value

	override fun setAreaGross(area: BigDecimal?) {
		_areaGross.value = area
	}

	override fun getAreaNet(): BigDecimal? = _areaNet.value

	override fun setAreaNet(area: BigDecimal?) {
		_areaNet.value = area
	}

	override fun getNrOfFloorsAboveGround(): Int? = _nrOfFloorsAboveGround.value

	override fun setNrOfFloorsAboveGround(nrOfFloors: Int?) {
		_nrOfFloorsAboveGround.value = nrOfFloors
	}

	override fun getNrOfFloorsBelowGround(): Int? = _nrOfFloorsBelowGround.value

	override fun setNrOfFloorsBelowGround(nrOfFloors: Int?) {
		_nrOfFloorsBelowGround.value = nrOfFloors
	}

	override fun getBuildingType(): CodeBuildingType? = _buildingType.value

	override fun setBuildingType(buildingType: CodeBuildingType?) {
		_buildingType.value = buildingType
	}

	override fun getBuildingSubType(): CodeBuildingSubType? = _buildingSubType.value

	override fun setBuildingSubType(buildingSubType: CodeBuildingSubType?) {
		_buildingSubType.value = buildingSubType
	}

	override fun getBuildingYear(): Int? = _buildingYear.value

	override fun setBuildingYear(buildingYear: Int?) {
		_buildingYear.value = buildingYear
	}

	override fun getInsuredValue(): BigDecimal? = _insuredValue.value

	override fun setInsuredValue(value: BigDecimal?) {
		_insuredValue.value = value
	}

	override fun getInsuredValueYear(): Int? = _insuredValueYear.value

	override fun setInsuredValueYear(year: Int?) {
		_insuredValueYear.value = year
	}

	override fun getNotInsuredValue(): BigDecimal? = _notInsuredValue.value

	override fun setNotInsuredValue(value: BigDecimal?) {
		_notInsuredValue.value = value
	}

	override fun getNotInsuredValueYear(): Int? = _notInsuredValueYear.value

	override fun setNotInsuredValueYear(year: Int?) {
		_notInsuredValueYear.value = year
	}

	override fun getThirdPartyValue(): BigDecimal? = _thirdPartyValue.value

	override fun setThirdPartyValue(value: BigDecimal?) {
		_thirdPartyValue.value = value
	}

	override fun getThirdPartyValueYear(): Int? = _thirdPartyValueYear.value

	override fun setThirdPartyValueYear(year: Int?) {
		_thirdPartyValueYear.value = year
	}

	override fun getCurrentRatingForView(): ObjBuildingPartRating? = getCurrentRating()

	override fun getRatingCount(): Int = _ratingList.partCount

	override fun getRating(seqNr: Int?): ObjBuildingPartRating? = _ratingList.getPart(seqNr)

	override fun getRatingList(): List<ObjBuildingPartRating> = _ratingList.parts

	override fun getRatingById(ratingId: Int?): ObjBuildingPartRating? {
		return _ratingList.parts.find { it.id == ratingId }
	}

	override fun getContactSet(): Set<Int> = _contactSet.items.mapNotNull { it as? Int }.toSet()

	override fun clearContactSet() {
		_contactSet.clearItems()
	}

	override fun addContact(contactId: Int?) {
		_contactSet.addItem(contactId)
	}

	override fun removeContact(contactId: Int?) {
		_contactSet.removeItem(contactId)
	}

	override fun getTasks(): List<DocTask> = taskRepository().getByForeignKey("related_obj_id", id)

	override fun addTask(): DocTask {
		val task = taskRepository().create(tenantId, null, OffsetDateTime.now())
		task.relatedToId = id as Int
		return task
	}
}

