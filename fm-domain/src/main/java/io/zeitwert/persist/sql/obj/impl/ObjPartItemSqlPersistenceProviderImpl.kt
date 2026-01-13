package io.zeitwert.persist.sql.obj.impl

import dddrive.ddd.model.Aggregate
import io.zeitwert.app.obj.model.db.Tables
import io.zeitwert.app.obj.model.db.tables.records.ObjPartItemRecord
import org.jooq.DSLContext
import org.slf4j.LoggerFactory

class ObjPartItemSqlPersistenceProviderImpl(
	val dslContext: DSLContext,
	val aggregate: Aggregate,
) {

	companion object {

		val logger = LoggerFactory.getLogger(ObjPartItemSqlPersistenceProviderImpl::class.java)!!
	}

	private val partsLoaded = mutableListOf<ObjPartItemRecord>()
	private val partsToInsert = mutableListOf<ObjPartItemRecord>()
	private val partsToUpdate = mutableListOf<ObjPartItemRecord>()

	/**
	 * Load all parts within a load sequence.
	 */
	fun doLoadParts(block: ObjPartItemSqlPersistenceProviderImpl.() -> Unit) {
		beginLoad()
		return try {
			block()
		} finally {
			endLoad()
		}
	}

	fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.OBJ_PART_ITEM,
				Tables.OBJ_PART_ITEM.OBJ_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
		logger.debug("objPartItemPP.beginLoad({}): {} items", aggregate.id, partsLoaded.size)
	}

	fun items(
		partListTypeId: String,
	) = items(0, partListTypeId)

	fun items(
		parentPartId: Int,
		partListTypeId: String,
	): List<String> {
		val items = partsLoaded
			.filter { (it.parentPartId == parentPartId) && (it.partListTypeId == partListTypeId) }
			.map { it.itemId }
		logger.debug("objPartItemPP.items({}, {}, {}): {} items", aggregate.id, parentPartId, partListTypeId, items.size)
		return items
	}

	fun endLoad() {
		partsLoaded.clear()
	}

	fun doStoreParts(block: ObjPartItemSqlPersistenceProviderImpl.() -> Unit) {
		beginStore()
		return try {
			block()
		} finally {
			endStore()
		}
	}

	fun beginStore() {
		logger.debug("beginStore({})", aggregate.id)
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun addItem(
		partListTypeId: String,
		item: String,
	) = addItem(0, partListTypeId, item)

	fun addItems(
		partListTypeId: String,
		items: List<String>,
	) = addItems(0, partListTypeId, items)

	fun addItem(
		parentPartId: Int,
		partListTypeId: String,
		item: String,
	) = addItems(parentPartId, partListTypeId, listOf(item))

	fun addItems(
		parentPartId: Int,
		partListTypeId: String,
		items: List<String>,
	) {
		logger.debug(
			"addItems({}, {}, {}, {} items)",
			aggregate.id,
			parentPartId,
			partListTypeId,
			items.size,
		)
		items.forEachIndexed { idx, it ->
			val record = mapToRecord(
				itemId = it,
				parentPartId = parentPartId,
				partListTypeId = partListTypeId,
				seqNr = idx + 1,
			)
			partsToInsert.add(record)
		}
	}

	fun endStore() {
		logger.debug(
			"endStore({}): {} to insert, {} to update",
			aggregate.id,
			partsToInsert.size,
			partsToUpdate.size,
		)
		// Update existing parts
		partsToUpdate.forEach { record ->
			record.update()
		}
		// Delete removed i.e. non-updated parts
		dslContext
			.deleteFrom(Tables.OBJ_PART_ITEM)
			.where(Tables.OBJ_PART_ITEM.OBJ_ID.eq(aggregate.id as Int))
			.and(Tables.OBJ_PART_ITEM.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun mapToRecord(
		itemId: String,
		parentPartId: Int?,
		partListTypeId: String,
		seqNr: Int,
	): ObjPartItemRecord {
		val record = dslContext.newRecord(Tables.OBJ_PART_ITEM)

		record.objId = aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.itemId = itemId

		record.aver = aggregate.meta.version

		return record
	}

}
