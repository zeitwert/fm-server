package io.zeitwert.persist.sql.doc

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPartTransition
import dddrive.ddd.model.Aggregate
import dddrive.ddd.model.Part
import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PartListProperty
import io.zeitwert.app.doc.model.db.Tables
import io.zeitwert.app.doc.model.db.tables.records.DocPartTransitionRecord
import io.zeitwert.persist.sql.ddd.PartSqlPersistenceProvider
import org.jooq.DSLContext

class DocPartTransitionSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val aggregate: Aggregate,
) : PartSqlPersistenceProvider<Doc, DocPartTransition> {

	private val partsLoaded = mutableListOf<DocPartTransitionRecord>()
	private val partsToInsert = mutableListOf<DocPartTransitionRecord>()
	private val partsToUpdate = mutableListOf<DocPartTransitionRecord>()

	override fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.DOC_PART_TRANSITION,
				Tables.DOC_PART_TRANSITION.DOC_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	override fun loadPartList(
		partList: PartListProperty<Doc, DocPartTransition>,
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
		part: DocPartTransition,
		record: DocPartTransitionRecord,
	) {
		part.setValueByPath("userId", record.userId)
		part.setValueByPath("timestamp", record.timestamp)
		part.setValueByPath("oldCaseStageId", record.oldCaseStageId)
		part.setValueByPath("newCaseStageId", record.newCaseStageId)
	}

	override fun beginStore() {
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	override fun storePartList(
		partList: PartListProperty<Doc, DocPartTransition>,
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
			.deleteFrom(Tables.DOC_PART_TRANSITION)
			.where(Tables.DOC_PART_TRANSITION.DOC_ID.eq(aggregate.id as Int))
			.and(Tables.DOC_PART_TRANSITION.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun mapToRecord(
		part: DocPartTransition,
		parentPartId: Int?,
		partListTypeId: String,
		seqNr: Int,
	): DocPartTransitionRecord {
		val record = dslContext.newRecord(Tables.DOC_PART_TRANSITION)

		record.id = part.id
		record.tenantId = part.meta.aggregate.tenantId as Int
		record.docId = part.meta.aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.userId = part.userId as Int
		record.timestamp = part.timestamp
		record.oldCaseStageId = part.oldCaseStage?.id
		record.newCaseStageId = part.newCaseStage.id

		record.aver = aggregate.meta.version

		return record
	}

}
