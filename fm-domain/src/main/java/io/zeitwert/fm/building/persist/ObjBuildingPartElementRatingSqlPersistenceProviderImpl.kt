package io.zeitwert.fm.building.persist

import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.property.model.PartListProperty
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating
import io.zeitwert.fm.building.model.db.Tables
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartElementRatingRecord
import io.zeitwert.fm.building.model.enums.CodeBuildingPart
import io.zeitwert.persist.sql.PartSqlPersistenceProvider
import org.jooq.DSLContext

class ObjBuildingPartElementRatingSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val aggregate: Aggregate,
) : PartSqlPersistenceProvider<ObjBuilding, ObjBuildingPartElementRating> {

	private val partsLoaded = mutableListOf<ObjBuildingPartElementRatingRecord>()
	private val partsToInsert = mutableListOf<ObjBuildingPartElementRatingRecord>()
	private val partsToUpdate = mutableListOf<ObjBuildingPartElementRatingRecord>()

	override fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.OBJ_BUILDING_PART_ELEMENT_RATING,
				Tables.OBJ_BUILDING_PART_ELEMENT_RATING.OBJ_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	override fun loadPartList(
		partList: PartListProperty<ObjBuilding, ObjBuildingPartElementRating>,
		partListTypeId: String,
	) {
		if ((partList.entity as? Part<*>) != null) {
			val parentPartId = (partList.entity as Part<*>).id
			partsLoaded.filter { (it.parentPartId == parentPartId) && (it.partListTypeId == partListTypeId) }
		} else {
			partsLoaded.filter { (it.parentPartId == 0) && (it.partListTypeId == partListTypeId) }
		}.forEach {
			val part = partList.add(it.id)
			mapFromRecord(
				part = part,
				record = it,
			)
		}
	}

	override fun endLoad() {
		partsLoaded.clear()
	}

	fun mapFromRecord(
		part: ObjBuildingPartElementRating,
		record: ObjBuildingPartElementRatingRecord,
	) {
		part.buildingPart = CodeBuildingPart.getBuildingPart(record.buildingPartId)
		part.weight = record.weight
		part.condition = record.condition
		part.ratingYear = record.conditionYear
		part.strain = record.strain
		part.strength = record.strength
		part.description = record.description
		part.conditionDescription = record.conditionDescription
		part.measureDescription = record.measureDescription
	}

	override fun beginStore() {
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	override fun storePartList(
		partList: PartListProperty<ObjBuilding, ObjBuildingPartElementRating>,
		partListTypeId: String,
	) {
		val parentPartId = (partList.entity as? Part<*>)?.id
		partList.forEachIndexed { idx, it ->
			val record = mapToRecord(
				part = it,
				parentPartId = parentPartId,
				partListTypeId = partListTypeId,
				seqNr = idx + 1,
			)
			if (it.meta.isNew) {
				partsToInsert.add(record)
			} else {
				partsToUpdate.add(record)
			}
		}
	}

	override fun endStore() {
		// Update existing parts
		partsToUpdate.forEach { record ->
			record.update()
		}
		// Delete removed i.e. non-updated parts
		dslContext
			.deleteFrom(Tables.OBJ_BUILDING_PART_ELEMENT_RATING)
			.where(Tables.OBJ_BUILDING_PART_ELEMENT_RATING.OBJ_ID.eq(aggregate.id as Int))
			.and(Tables.OBJ_BUILDING_PART_ELEMENT_RATING.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun mapToRecord(
		part: ObjBuildingPartElementRating,
		parentPartId: Int?,
		partListTypeId: String,
		seqNr: Int,
	): ObjBuildingPartElementRatingRecord {
		val record = dslContext.newRecord(Tables.OBJ_BUILDING_PART_ELEMENT_RATING)

		record.id = part.id
		record.objId = part.meta.aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.buildingPartId = part.buildingPart?.id
		record.weight = part.weight
		record.condition = part.condition
		record.conditionYear = part.ratingYear
		record.strain = part.strain
		record.strength = part.strength
		record.description = part.description
		record.conditionDescription = part.conditionDescription
		record.measureDescription = part.measureDescription

		record.aver = aggregate.meta.version

		return record
	}

}
