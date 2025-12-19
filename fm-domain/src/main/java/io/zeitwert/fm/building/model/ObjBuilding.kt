package io.zeitwert.fm.building.model

import io.dddrive.core.obj.model.Obj
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.dms.model.ObjDocument
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.fm.task.model.ItemWithTasks
import java.math.BigDecimal
import java.time.OffsetDateTime

interface ObjBuilding :
	Obj,
	ItemWithAccount,
	ItemWithNotes,
	ItemWithTasks {

	var name: String?

	var description: String?

	var buildingNr: String?

	var insuranceNr: String?

	var plotNr: String?

	var nationalBuildingId: String?

	var historicPreservation: CodeHistoricPreservation?

	var street: String?

	var zip: String?

	var city: String?

	var country: CodeCountry?

	var geoAddress: String?

	var geoCoordinates: String?

	var geoZoom: Int?

	val coverFotoId: Int?

	val coverFoto: ObjDocument?

	var currency: CodeCurrency?

	var volume: BigDecimal?

	var areaGross: BigDecimal?

	var areaNet: BigDecimal?

	var nrOfFloorsAboveGround: Int?

	var nrOfFloorsBelowGround: Int?

	var buildingType: CodeBuildingType?

	var buildingSubType: CodeBuildingSubType?

	var buildingYear: Int?

	var insuredValue: BigDecimal?

	var insuredValueYear: Int?

	var notInsuredValue: BigDecimal?

	var notInsuredValueYear: Int?

	var thirdPartyValue: BigDecimal?

	var thirdPartyValueYear: Int?

	val currentRating: ObjBuildingPartRating?

	val currentRatingForView: ObjBuildingPartRating?

	val ratingCount: Int

	fun getRating(seqNr: Int): ObjBuildingPartRating

	val ratingList: List<ObjBuildingPartRating>

	fun getRatingById(ratingId: Int): ObjBuildingPartRating

	fun addRating(
		user: ObjUserFM,
		timestamp: OffsetDateTime,
	): ObjBuildingPartRating

	fun removeRating(ratingId: Int)

	val inflationRate: Double

	val discountRate: Double

	fun getCondition(year: Int): Int?

	fun getBuildingValue(year: Int): Double

	val contactSet: Set<Int>

	fun clearContactSet()

	fun addContact(contactId: Int)

	fun removeContact(contactId: Int)

}
