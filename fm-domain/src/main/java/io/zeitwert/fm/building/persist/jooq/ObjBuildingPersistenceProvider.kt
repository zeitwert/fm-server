package io.zeitwert.fm.building.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingRepository
import io.zeitwert.fm.building.model.db.Tables
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType
import io.zeitwert.fm.building.model.enums.CodeBuildingType
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.oe.model.enums.CodeCountry
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component("objBuildingPersistenceProvider")
open class ObjBuildingPersistenceProvider : JooqObjPersistenceProviderBase<ObjBuilding>() {

    private lateinit var _dslContext: DSLContext
    private lateinit var _repository: ObjBuildingRepository

    @Autowired
    fun setDslContext(dslContext: DSLContext) {
        this._dslContext = dslContext
    }

    @Autowired
    @Lazy
    fun setRepository(repository: ObjBuildingRepository) {
        this._repository = repository
    }

    override fun dslContext(): DSLContext = _dslContext

    override fun getRepository(): ObjBuildingRepository = _repository

    override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

    override fun nextAggregateId(): Any {
        return dslContext()
            .nextval(Sequences.OBJ_ID_SEQ)
            .toInt()
    }

    override fun fromAggregate(aggregate: ObjBuilding): UpdatableRecord<*> {
        return createObjRecord(aggregate)
    }

    @Suppress("UNCHECKED_CAST", "LongMethod")
    override fun storeExtension(aggregate: ObjBuilding) {
        val objId = aggregate.id as Int

        val existingRecord = dslContext().fetchOne(
            Tables.OBJ_BUILDING,
            Tables.OBJ_BUILDING.OBJ_ID.eq(objId)
        )

        val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_BUILDING)

        record.objId = objId
        record.tenantId = aggregate.tenantId as? Int
        record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
        record.name = (aggregate.getProperty("name") as? BaseProperty<String?>)?.value
        record.description = (aggregate.getProperty("description") as? BaseProperty<String?>)?.value
        record.buildingNr = (aggregate.getProperty("buildingNr") as? BaseProperty<String?>)?.value
        record.insuranceNr = (aggregate.getProperty("insuranceNr") as? BaseProperty<String?>)?.value
        record.plotNr = (aggregate.getProperty("plotNr") as? BaseProperty<String?>)?.value
        record.nationalBuildingId = (aggregate.getProperty("nationalBuilding") as? BaseProperty<String?>)?.value
        record.historicPreservationId = (aggregate.getProperty("historicPreservation") as? EnumProperty<CodeHistoricPreservation>)?.value?.id
        record.street = (aggregate.getProperty("street") as? BaseProperty<String?>)?.value
        record.zip = (aggregate.getProperty("zip") as? BaseProperty<String?>)?.value
        record.city = (aggregate.getProperty("city") as? BaseProperty<String?>)?.value
        record.countryId = (aggregate.getProperty("country") as? EnumProperty<CodeCountry>)?.value?.id
        record.geoAddress = (aggregate.getProperty("geoAddress") as? BaseProperty<String?>)?.value
        record.geoCoordinates = (aggregate.getProperty("geoCoordinates") as? BaseProperty<String?>)?.value
        record.geoZoom = (aggregate.getProperty("geoZoom") as? BaseProperty<Int?>)?.value
        record.coverFotoId = (aggregate.getProperty("coverFoto") as? ReferenceProperty<*>)?.id as? Int
        record.currencyId = (aggregate.getProperty("currency") as? EnumProperty<CodeCurrency>)?.value?.id
        record.volume = (aggregate.getProperty("volume") as? BaseProperty<BigDecimal?>)?.value
        record.areaGross = (aggregate.getProperty("areaGross") as? BaseProperty<BigDecimal?>)?.value
        record.areaNet = (aggregate.getProperty("areaNet") as? BaseProperty<BigDecimal?>)?.value
        record.nrOfFloorsAboveGround = (aggregate.getProperty("nrOfFloorsAboveGround") as? BaseProperty<Int?>)?.value
        record.nrOfFloorsBelowGround = (aggregate.getProperty("nrOfFloorsBelowGround") as? BaseProperty<Int?>)?.value
        record.buildingTypeId = (aggregate.getProperty("buildingType") as? EnumProperty<CodeBuildingType>)?.value?.id
        record.buildingSubTypeId = (aggregate.getProperty("buildingSubType") as? EnumProperty<CodeBuildingSubType>)?.value?.id
        record.buildingYear = (aggregate.getProperty("buildingYear") as? BaseProperty<Int?>)?.value
        record.insuredValue = (aggregate.getProperty("insuredValue") as? BaseProperty<BigDecimal?>)?.value
        record.insuredValueYear = (aggregate.getProperty("insuredValueYear") as? BaseProperty<Int?>)?.value
        record.notInsuredValue = (aggregate.getProperty("notInsuredValue") as? BaseProperty<BigDecimal?>)?.value
        record.notInsuredValueYear = (aggregate.getProperty("notInsuredValueYear") as? BaseProperty<Int?>)?.value
        record.thirdPartyValue = (aggregate.getProperty("thirdPartyValue") as? BaseProperty<BigDecimal?>)?.value
        record.thirdPartyValueYear = (aggregate.getProperty("thirdPartyValueYear") as? BaseProperty<Int?>)?.value

        if (existingRecord != null) {
            record.update()
        } else {
            record.insert()
        }
    }

    @Suppress("UNCHECKED_CAST", "LongMethod")
    override fun loadExtension(aggregate: ObjBuilding, objId: Int?) {
        if (objId == null) return

        val record = dslContext().fetchOne(
            Tables.OBJ_BUILDING,
            Tables.OBJ_BUILDING.OBJ_ID.eq(objId)
        ) ?: return

        (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.accountId
        (aggregate.getProperty("name") as? BaseProperty<String?>)?.value = record.name
        (aggregate.getProperty("description") as? BaseProperty<String?>)?.value = record.description
        (aggregate.getProperty("buildingNr") as? BaseProperty<String?>)?.value = record.buildingNr
        (aggregate.getProperty("insuranceNr") as? BaseProperty<String?>)?.value = record.insuranceNr
        (aggregate.getProperty("plotNr") as? BaseProperty<String?>)?.value = record.plotNr
        (aggregate.getProperty("nationalBuilding") as? BaseProperty<String?>)?.value = record.nationalBuildingId

        record.historicPreservationId?.let { id ->
            (aggregate.getProperty("historicPreservation") as? EnumProperty<CodeHistoricPreservation>)?.value =
                CodeHistoricPreservation.getHistoricPreservation(id)
        }

        (aggregate.getProperty("street") as? BaseProperty<String?>)?.value = record.street
        (aggregate.getProperty("zip") as? BaseProperty<String?>)?.value = record.zip
        (aggregate.getProperty("city") as? BaseProperty<String?>)?.value = record.city

        record.countryId?.let { id ->
            (aggregate.getProperty("country") as? EnumProperty<CodeCountry>)?.value =
                CodeCountry.getCountry(id)
        }

        (aggregate.getProperty("geoAddress") as? BaseProperty<String?>)?.value = record.geoAddress
        (aggregate.getProperty("geoCoordinates") as? BaseProperty<String?>)?.value = record.geoCoordinates
        (aggregate.getProperty("geoZoom") as? BaseProperty<Int?>)?.value = record.geoZoom

        (aggregate.getProperty("coverFoto") as? ReferenceProperty<*>)?.let { prop ->
            @Suppress("UNCHECKED_CAST")
            (prop as ReferenceProperty<Any>).id = record.coverFotoId
        }

        record.currencyId?.let { id ->
            (aggregate.getProperty("currency") as? EnumProperty<CodeCurrency>)?.value =
                CodeCurrency.getCurrency(id)
        }

        (aggregate.getProperty("volume") as? BaseProperty<BigDecimal?>)?.value = record.volume
        (aggregate.getProperty("areaGross") as? BaseProperty<BigDecimal?>)?.value = record.areaGross
        (aggregate.getProperty("areaNet") as? BaseProperty<BigDecimal?>)?.value = record.areaNet
        (aggregate.getProperty("nrOfFloorsAboveGround") as? BaseProperty<Int?>)?.value = record.nrOfFloorsAboveGround
        (aggregate.getProperty("nrOfFloorsBelowGround") as? BaseProperty<Int?>)?.value = record.nrOfFloorsBelowGround

        record.buildingTypeId?.let { id ->
            (aggregate.getProperty("buildingType") as? EnumProperty<CodeBuildingType>)?.value =
                CodeBuildingType.getBuildingType(id)
        }

        record.buildingSubTypeId?.let { id ->
            (aggregate.getProperty("buildingSubType") as? EnumProperty<CodeBuildingSubType>)?.value =
                CodeBuildingSubType.getBuildingSubType(id)
        }

        (aggregate.getProperty("buildingYear") as? BaseProperty<Int?>)?.value = record.buildingYear
        (aggregate.getProperty("insuredValue") as? BaseProperty<BigDecimal?>)?.value = record.insuredValue
        (aggregate.getProperty("insuredValueYear") as? BaseProperty<Int?>)?.value = record.insuredValueYear
        (aggregate.getProperty("notInsuredValue") as? BaseProperty<BigDecimal?>)?.value = record.notInsuredValue
        (aggregate.getProperty("notInsuredValueYear") as? BaseProperty<Int?>)?.value = record.notInsuredValueYear
        (aggregate.getProperty("thirdPartyValue") as? BaseProperty<BigDecimal?>)?.value = record.thirdPartyValue
        (aggregate.getProperty("thirdPartyValueYear") as? BaseProperty<Int?>)?.value = record.thirdPartyValueYear
    }

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_building"
    }
}

