package io.zeitwert.fm.building.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.db.Tables
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objBuildingPersistenceProvider")
open class ObjBuildingPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjBuilding, ObjRecord, ObjBuildingRecord>(ObjBuilding::class.java),
	SqlAggregateRecordMapper<ObjBuilding, ObjBuildingRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjBuildingRecord {
		val record = dslContext.fetchOne(Tables.OBJ_BUILDING, Tables.OBJ_BUILDING.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_BUILDING record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST", "LongMethod")
	override fun mapFromRecord(
		aggregate: ObjBuilding,
		record: ObjBuildingRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.setValueByPath("name", record.name)
		aggregate.setValueByPath("description", record.description)
		aggregate.setValueByPath("buildingNr", record.buildingNr)
		aggregate.setValueByPath("insuranceNr", record.insuranceNr)
		aggregate.setValueByPath("plotNr", record.plotNr)
		aggregate.setValueByPath("nationalBuildingId", record.nationalBuildingId)
		aggregate.setValueByPath("historicPreservationId", record.historicPreservationId)
		aggregate.setValueByPath("street", record.street)
		aggregate.setValueByPath("zip", record.zip)
		aggregate.setValueByPath("city", record.city)
		aggregate.setValueByPath("countryId", record.countryId)
		aggregate.setValueByPath("geoAddress", record.geoAddress)
		aggregate.setValueByPath("geoCoordinates", record.geoCoordinates)
		aggregate.setValueByPath("geoZoom", record.geoZoom)
		aggregate.setValueByPath("coverFotoId", record.coverFotoId)
		aggregate.setValueByPath("currencyId", record.currencyId)
		aggregate.setValueByPath("volume", record.volume)
		aggregate.setValueByPath("areaGross", record.areaGross)
		aggregate.setValueByPath("areaNet", record.areaNet)
		aggregate.setValueByPath("nrOfFloorsAboveGround", record.nrOfFloorsAboveGround)
		aggregate.setValueByPath("nrOfFloorsBelowGround", record.nrOfFloorsBelowGround)
		aggregate.setValueByPath("buildingTypeId", record.buildingTypeId)
		aggregate.setValueByPath("buildingSubTypeId", record.buildingSubTypeId)
		aggregate.setValueByPath("buildingYear", record.buildingYear)
		aggregate.setValueByPath("insuredValue", record.insuredValue)
		aggregate.setValueByPath("insuredValueYear", record.insuredValueYear)
		aggregate.setValueByPath("notInsuredValue", record.notInsuredValue)
		aggregate.setValueByPath("notInsuredValueYear", record.notInsuredValueYear)
		aggregate.setValueByPath("thirdPartyValue", record.thirdPartyValue)
		aggregate.setValueByPath("thirdPartyValueYear", record.thirdPartyValueYear)
	}

	@Suppress("UNCHECKED_CAST", "LongMethod")
	override fun mapToRecord(aggregate: ObjBuilding): ObjBuildingRecord {
		val record = dslContext.newRecord(Tables.OBJ_BUILDING)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.name = aggregate.name
		record.description = aggregate.description
		record.buildingNr = aggregate.buildingNr
		record.insuranceNr = aggregate.insuranceNr
		record.plotNr = aggregate.plotNr
		record.nationalBuildingId = aggregate.nationalBuildingId
		record.historicPreservationId = aggregate.historicPreservation?.id
		record.street = aggregate.street
		record.zip = aggregate.zip
		record.city = aggregate.city
		record.countryId = aggregate.country?.id
		record.geoAddress = aggregate.geoAddress
		record.geoCoordinates = aggregate.geoCoordinates
		record.geoZoom = aggregate.geoZoom
		record.coverFotoId = aggregate.coverFotoId
		record.currencyId = aggregate.currency?.id
		record.volume = aggregate.volume
		record.areaGross = aggregate.areaGross
		record.areaNet = aggregate.areaNet
		record.nrOfFloorsAboveGround = aggregate.nrOfFloorsAboveGround
		record.nrOfFloorsBelowGround = aggregate.nrOfFloorsBelowGround
		record.buildingTypeId = aggregate.buildingType?.id
		record.buildingSubTypeId = aggregate.buildingSubType?.id
		record.buildingYear = aggregate.buildingYear
		record.insuredValue = aggregate.insuredValue
		record.insuredValueYear = aggregate.insuredValueYear
		record.notInsuredValue = aggregate.notInsuredValue
		record.notInsuredValueYear = aggregate.notInsuredValueYear
		record.thirdPartyValue = aggregate.thirdPartyValue
		record.thirdPartyValueYear = aggregate.thirdPartyValueYear

		return record
	}

	override fun storeRecord(
		record: ObjBuildingRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_BUILDING.OBJ_ID)
			.from(Tables.OBJ_BUILDING)
			.where(Tables.OBJ_BUILDING.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_BUILDING.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ_BUILDING.TENANT_ID
			"accountId" -> Tables.OBJ_BUILDING.ACCOUNT_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_BUILDING.OBJ_ID)
			.from(Tables.OBJ_BUILDING)
			.where(field.eq(targetId as Int))
			.fetch(Tables.OBJ_BUILDING.OBJ_ID)
	}

}
