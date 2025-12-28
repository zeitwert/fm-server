package io.zeitwert.fm.obj.persist

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPartTransition
import dddrive.ddd.core.model.Aggregate
import dddrive.ddd.core.model.Part
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PartListProperty
import io.zeitwert.dddrive.persist.PartSqlPersistenceProvider
import io.zeitwert.fm.obj.model.db.Tables
import io.zeitwert.fm.obj.model.db.tables.records.ObjPartTransitionRecord
import org.jooq.DSLContext

class ObjPartTransitionSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val aggregate: Aggregate,
) : PartSqlPersistenceProvider<Obj, ObjPartTransition> {

	private val partsLoaded = mutableListOf<ObjPartTransitionRecord>()
	private val partsToInsert = mutableListOf<ObjPartTransitionRecord>()
	private val partsToUpdate = mutableListOf<ObjPartTransitionRecord>()

	override fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.OBJ_PART_TRANSITION,
				Tables.OBJ_PART_TRANSITION.OBJ_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	override fun loadPartList(
		partList: PartListProperty<Obj, ObjPartTransition>,
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
		part: ObjPartTransition,
		record: ObjPartTransitionRecord,
	) {
		part.setValueByPath("userId", record.userId)
		part.setValueByPath("timestamp", record.timestamp)
	}

	override fun beginStore() {
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	override fun storePartList(
		partList: PartListProperty<Obj, ObjPartTransition>,
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
			.deleteFrom(Tables.OBJ_PART_TRANSITION)
			.where(Tables.OBJ_PART_TRANSITION.OBJ_ID.eq(aggregate.id as Int))
			.and(Tables.OBJ_PART_TRANSITION.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun mapToRecord(
		part: ObjPartTransition,
		parentPartId: Int?,
		partListTypeId: String,
		seqNr: Int,
	): ObjPartTransitionRecord {
		val record = dslContext.newRecord(Tables.OBJ_PART_TRANSITION)

		record.id = part.id
		record.tenantId = part.meta.aggregate.tenantId as Int
		record.objId = part.meta.aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.userId = part.userId as Int
		record.timestamp = part.timestamp

		record.aver = aggregate.meta.version

		return record
	}

}
