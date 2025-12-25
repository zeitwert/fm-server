package io.zeitwert.fm.test.persist

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part
import io.dddrive.property.model.PartListProperty
import io.zeitwert.dddrive.persist.PartSqlPersistenceProvider
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.db.Tables
import io.zeitwert.fm.test.model.db.tables.records.ObjTestPartNodeRecord
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.DSLContext

class ObjTestPartNodeSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val aggregate: Aggregate,
) : PartSqlPersistenceProvider<ObjTestPartNode> {

	private val partsLoaded = mutableListOf<ObjTestPartNodeRecord>()
	private val partsToInsert = mutableListOf<ObjTestPartNodeRecord>()
	private val partsToUpdate = mutableListOf<ObjTestPartNodeRecord>()

	override fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.OBJ_TEST_PART_NODE,
				Tables.OBJ_TEST_PART_NODE.OBJ_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	override fun loadPartList(
		partList: PartListProperty<ObjTestPartNode>,
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
		part: ObjTestPartNode,
		record: ObjTestPartNodeRecord,
	) {
		part.shortText = record.shortText
		part.longText = record.longText
		part.date = record.date
		part.int = record.int
		part.isDone = record.isDone
		part.json = record.json?.toString()
		part.nr = record.nr
		part.testType = CodeTestType.getTestType(record.testTypeId)
		part.refObjId = record.refObjId
	}

	override fun beginStore() {
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	override fun storePartList(
		partList: PartListProperty<ObjTestPartNode>,
		partListTypeId: String,
	) {
		val parentPartId = (partList.entity as? Part<*>)?.id
		partList.forEach {
			val record = mapToRecord(
				part = it,
				parentPartId = parentPartId,
				partListTypeId = partListTypeId,
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
			.deleteFrom(Tables.OBJ_TEST_PART_NODE)
			.where(Tables.OBJ_TEST_PART_NODE.OBJ_ID.eq(aggregate.id as Int))
			.and(Tables.OBJ_TEST_PART_NODE.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun mapToRecord(
		part: ObjTestPartNode,
		parentPartId: Int?,
		partListTypeId: String,
	): ObjTestPartNodeRecord {
		val record = dslContext.newRecord(Tables.OBJ_TEST_PART_NODE)

		record.id = part.id
		record.objId = part.meta.aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId

		record.shortText = part.shortText
		record.longText = part.longText
		record.date = part.date
		record.int = part.int
		record.isDone = part.isDone
		record.json = part.json?.let { org.jooq.JSON.valueOf(it) }
		record.nr = part.nr
		record.testTypeId = part.testType?.id
		record.refObjId = part.refObjId as Int?

		record.aver = aggregate.meta.version

		return record
	}

}
