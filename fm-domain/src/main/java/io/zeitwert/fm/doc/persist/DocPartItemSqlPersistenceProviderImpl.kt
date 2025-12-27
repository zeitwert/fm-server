package io.zeitwert.fm.doc.persist

import dddrive.ddd.core.model.Aggregate
import io.zeitwert.fm.doc.model.db.Tables
import io.zeitwert.fm.doc.model.db.tables.records.DocPartItemRecord
import org.jooq.DSLContext

class DocPartItemSqlPersistenceProviderImpl(
	val dslContext: DSLContext,
	val aggregate: Aggregate,
) {

	private val partsLoaded = mutableListOf<DocPartItemRecord>()
	private val partsToInsert = mutableListOf<DocPartItemRecord>()
	private val partsToUpdate = mutableListOf<DocPartItemRecord>()

	fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.DOC_PART_ITEM,
				Tables.DOC_PART_ITEM.DOC_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	fun items(
		partListTypeId: String,
	) = items(0, partListTypeId)

	fun items(
		parentPartId: Int,
		partListTypeId: String,
	): List<String> =
		partsLoaded
			.filter { (it.parentPartId == parentPartId) && (it.partListTypeId == partListTypeId) }
			.map { it.itemId }

	fun endLoad() {
		partsLoaded.clear()
	}

	fun beginStore() {
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
		// Update existing parts
		partsToUpdate.forEach { record ->
			record.update()
		}
		// Delete removed i.e. non-updated parts
		dslContext
			.deleteFrom(Tables.DOC_PART_ITEM)
			.where(Tables.DOC_PART_ITEM.DOC_ID.eq(aggregate.id as Int))
			.and(Tables.DOC_PART_ITEM.AVER.lt(aggregate.meta.version))
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
	): DocPartItemRecord {
		val record = dslContext.newRecord(Tables.DOC_PART_ITEM)

		record.docId = aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.itemId = itemId

		record.aver = aggregate.meta.version

		return record
	}

}
