package io.zeitwert.persist.sql.obj.impl

import dddrive.query.QuerySpec
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.db.Tables
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingRecord
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.fm.oe.model.enums.CodeCountry
import io.zeitwert.persist.ObjBuildingPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.obj.base.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component("objBuildingPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class ObjBuildingSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjSqlPersistenceProviderBase<ObjBuilding>(ObjBuilding::class.java),
	SqlRecordMapper<ObjBuilding>,
	ObjBuildingPersistenceProvider {

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
		aggregate.accountId = record.accountId
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
		aggregate.coverFotoId = record.coverFotoId
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
	override fun loadParts(aggregate: ObjBuilding) {
		super.loadParts(aggregate)
		// Load ratings
		ObjBuildingPartRatingSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate.ratingList, "building.ratingList")
		}
		// Load element ratings for each rating
		ObjBuildingPartElementRatingSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			aggregate.ratingList.forEach { rating ->
				loadPartList(rating.elementList, "building.elementRatingList")
			}
		}
		// Load contact set
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			items("building.contactSet").forEach {
				aggregate.contactSet.add(it.toInt())
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun storeParts(aggregate: ObjBuilding) {
		super.storeParts(aggregate)
		// Store ratings
		ObjBuildingPartRatingSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate.ratingList, "building.ratingList")
		}
		// Store element ratings for each rating
		ObjBuildingPartElementRatingSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			aggregate.ratingList.forEach { rating ->
				storePartList(rating.elementList, "building.elementRatingList")
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
		record.accountId = aggregate.accountId as Int

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
		record.coverFotoId = aggregate.coverFotoId as? Int
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

}
