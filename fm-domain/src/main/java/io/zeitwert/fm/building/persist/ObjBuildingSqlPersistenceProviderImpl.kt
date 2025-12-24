package io.zeitwert.fm.building.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.db.Tables
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjPartItemSqlPersistenceProviderImpl
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.fm.app.model.RequestContextFM
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objBuildingPersistenceProvider")
open class ObjBuildingSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val requestCtx: RequestContextFM,
) : FMObjSqlPersistenceProviderBase<ObjBuilding>(ObjBuilding::class.java),
	SqlRecordMapper<ObjBuilding> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjBuilding) {
		val record = dslContext.fetchOne(Tables.OBJ_BUILDING, Tables.OBJ_BUILDING.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_BUILDING record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST", "LongMethod")
	private fun mapFromRecord(
		aggregate: ObjBuilding,
		record: ObjBuildingRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.buildingNr = record.buildingNr
		aggregate.insuranceNr = record.insuranceNr
		aggregate.plotNr = record.plotNr
		aggregate.nationalBuildingId = record.nationalBuildingId
		aggregate.historicPreservation = CodeHistoricPreservation.getHistoricPreservation(record.historicPreservationId)
		aggregate.street = record.street
		aggregate.zip = record.zip
		aggregate.city = record.city
		aggregate.country = CodeCountry.getCountry(record.countryId)
		aggregate.geoAddress = record.geoAddress
		aggregate.geoCoordinates = record.geoCoordinates
		aggregate.geoZoom = record.geoZoom
		aggregate.setValueByPath("coverFotoId", record.coverFotoId)
		aggregate.currency = CodeCurrency.getCurrency(record.currencyId)
		aggregate.volume = record.volume
		aggregate.areaGross = record.areaGross
		aggregate.areaNet = record.areaNet
		aggregate.nrOfFloorsAboveGround = record.nrOfFloorsAboveGround
		aggregate.nrOfFloorsBelowGround = record.nrOfFloorsBelowGround
		aggregate.buildingType = CodeBuildingType.getBuildingType(record.buildingTypeId)
		aggregate.buildingSubType = CodeBuildingSubType.getBuildingSubType(record.buildingSubTypeId)
		aggregate.buildingYear = record.buildingYear
		aggregate.insuredValue = record.insuredValue
		aggregate.insuredValueYear = record.insuredValueYear
		aggregate.notInsuredValue = record.notInsuredValue
		aggregate.notInsuredValueYear = record.notInsuredValueYear
		aggregate.thirdPartyValue = record.thirdPartyValue
		aggregate.thirdPartyValueYear = record.thirdPartyValueYear
	}

	override fun storeRecord(aggregate: ObjBuilding) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doLoadParts(aggregate: ObjBuilding) {
		super.doLoadParts(aggregate)
		// Load ratings
		ObjBuildingPartRatingSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate, "ratingList", "building.ratingList")
		}
		// Load element ratings for each rating
		ObjBuildingPartElementRatingSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			aggregate.ratingList.forEach { rating ->
				loadPartList(rating, "elementList", "building.elementRatingList")
			}
		}
		// Load contact set
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			items("building.contactSet").forEach {
				aggregate.addContact(it.toInt())
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doStoreParts(aggregate: ObjBuilding) {
		super.doStoreParts(aggregate)
		// Store ratings
		ObjBuildingPartRatingSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate, "ratingList", "building.ratingList")
		}
		// Store element ratings for each rating
		ObjBuildingPartElementRatingSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			aggregate.ratingList.forEach { rating ->
				storePartList(rating, "elementList", "building.elementRatingList")
			}
		}
		// Store contact set
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			addItems("building.contactSet", aggregate.contactSet.map { it.toString() })
		}
	}

	@Suppress("UNCHECKED_CAST", "LongMethod")
	private fun mapToRecord(aggregate: ObjBuilding): ObjBuildingRecord {
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

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_BUILDING_V, Tables.OBJ_BUILDING_V.ID, query)

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
