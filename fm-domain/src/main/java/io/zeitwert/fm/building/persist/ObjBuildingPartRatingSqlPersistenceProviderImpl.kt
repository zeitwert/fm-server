package io.zeitwert.fm.building.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.PartSqlPersistenceProvider
import io.zeitwert.fm.building.model.ObjBuildingPartRating
import io.zeitwert.fm.building.model.db.Tables
import io.zeitwert.fm.building.model.db.tables.records.ObjBuildingPartRatingRecord
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog
import io.zeitwert.fm.building.model.enums.CodeBuildingRatingStatus
import org.jooq.DSLContext

class ObjBuildingPartRatingSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val aggregate: Aggregate,
) : PartSqlPersistenceProvider<ObjBuildingPartRating> {

	private val partsLoaded = mutableListOf<ObjBuildingPartRatingRecord>()
	private val partsToInsert = mutableListOf<ObjBuildingPartRatingRecord>()
	private val partsToUpdate = mutableListOf<ObjBuildingPartRatingRecord>()

	override fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.OBJ_BUILDING_PART_RATING,
				Tables.OBJ_BUILDING_PART_RATING.OBJ_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	override fun loadPartList(
		partList: PartListProperty<ObjBuildingPartRating>,
		partListTypeId: String,
	) {
		if ((partList.entity as? Part<*>) != null) {
			val parentPartId = (partList.entity as Part<*>).id
			partsLoaded.filter { (it.parentPartId == parentPartId) && (it.partListTypeId == partListTypeId) }
		} else {
			partsLoaded.filter { (it.parentPartId == 0) && (it.partListTypeId == partListTypeId) }
		}.forEach {
			val part = partList.addPart(it.id)
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
		part: ObjBuildingPartRating,
		record: ObjBuildingPartRatingRecord,
	) {
		part.partCatalog = CodeBuildingPartCatalog.getPartCatalog(record.partCatalogId)
		part.maintenanceStrategy = CodeBuildingMaintenanceStrategy.getMaintenanceStrategy(record.maintenanceStrategyId)
		part.ratingStatus = CodeBuildingRatingStatus.getRatingStatus(record.ratingStatusId)
		part.ratingDate = record.ratingDate
		part.setValueByPath("ratingUserId", record.ratingUserId)
	}

	override fun beginStore() {
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	override fun storePartList(
		partList: PartListProperty<ObjBuildingPartRating>,
		partListTypeId: String,
	) {
		val parentPartId = (partList.entity as? Part<*>)?.id
		partList.parts.forEachIndexed { idx, it ->
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
			.deleteFrom(Tables.OBJ_BUILDING_PART_RATING)
			.where(Tables.OBJ_BUILDING_PART_RATING.OBJ_ID.eq(aggregate.id as Int))
			.and(Tables.OBJ_BUILDING_PART_RATING.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	@Suppress("UNCHECKED_CAST")
	fun mapToRecord(
		part: ObjBuildingPartRating,
		parentPartId: Int?,
		partListTypeId: String,
		seqNr: Int,
	): ObjBuildingPartRatingRecord {
		val record = dslContext.newRecord(Tables.OBJ_BUILDING_PART_RATING)

		record.id = part.id
		record.objId = part.meta.aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.partCatalogId = part.partCatalog?.id
		record.maintenanceStrategyId = part.maintenanceStrategy?.id
		record.ratingStatusId = part.ratingStatus?.id
		record.ratingDate = part.ratingDate
		record.ratingUserId = part.ratingUser?.id as? Int

		record.aver = aggregate.meta.version

		return record
	}

}
