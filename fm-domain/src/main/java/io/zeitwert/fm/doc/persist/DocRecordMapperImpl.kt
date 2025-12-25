package io.zeitwert.fm.doc.persist

import io.dddrive.ddd.model.Aggregate
import io.dddrive.ddd.model.Part
import io.dddrive.doc.model.Doc
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.doc.model.db.Sequences
import io.zeitwert.fm.doc.model.db.Tables
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord
import org.jooq.DSLContext

class DocRecordMapperImpl(
	val dslContext: DSLContext,
) : SqlRecordMapper<Aggregate>,
	SqlIdProvider {

	override fun nextAggregateId(): Any = dslContext.nextval(Sequences.DOC_ID_SEQ).toInt()

	override fun <P : Part<*>> nextPartId(
		aggregate: Aggregate,
		partClass: Class<P>,
	): Int = dslContext.nextval(Sequences.DOC_PART_ID_SEQ).toInt()
	// (aggregate as AggregateSPI).nextPartId(partClass)

	override fun loadRecord(aggregate: Aggregate) {
		val record = dslContext.fetchOne(Tables.DOC, Tables.DOC.ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no DOC record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: Aggregate,
		record: DocRecord,
	) {
		// doc base fields
		aggregate.setValueByPath("id", record.id)
		aggregate.setValueByPath("tenantId", record.tenantId)
		if (aggregate is ItemWithAccount) {
			aggregate.accountId = record.accountId
		}
		aggregate.setValueByPath("ownerId", record.ownerId)
		aggregate.setValueByPath("caption", record.caption)
		// doc-specific fields
		aggregate.setValueByPath("caseDefId", record.caseDefId)
		aggregate.setValueByPath("caseStageId", record.caseStageId)
		aggregate.setValueByPath("assigneeId", record.assigneeId)
		// doc_meta
		aggregate.setValueByPath("version", record.version)
		aggregate.setValueByPath("createdAt", record.createdAt)
		aggregate.setValueByPath("createdByUserId", record.createdByUserId)
		aggregate.setValueByPath("modifiedAt", record.modifiedAt)
		aggregate.setValueByPath("modifiedByUserId", record.modifiedByUserId)
	}

	override fun storeRecord(aggregate: Aggregate) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMDocBase).isNew) {
			record.insert()
		} else {
			record.changed(true)
			record.update()
		}
		// After store(), JOOQ has updated the record with the new version
		// Refresh the version in the aggregate so it can be used for parts
		(aggregate as Doc).setValueByPath("version", record.version)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: Aggregate): DocRecord {
		val record = dslContext.newRecord(Tables.DOC)
		aggregate as Doc

		record.id = aggregate.id as Int
		record.docTypeId = aggregate.meta.docTypeId
		record.tenantId = aggregate.tenantId as Int
		if (aggregate is ItemWithAccount) {
			record.accountId = aggregate.accountId as Int?
		}
		record.ownerId = aggregate.owner?.id as Int?
		record.caption = aggregate.caption

		record.caseDefId = aggregate.meta.caseDef?.id
		record.caseStageId = aggregate.meta.caseStage?.id
		record.isInWork = aggregate.meta.isInWork
		record.assigneeId = aggregate.assignee?.id as Int?

		record.version = aggregate.meta.version
		record.createdAt = aggregate.meta.createdAt
		record.createdByUserId = aggregate.meta.createdByUser?.id as Int?
		record.modifiedAt = aggregate.meta.modifiedAt
		record.modifiedByUserId = aggregate.meta.modifiedByUser?.id as? Int
		return record
	}

	override fun getAll(tenantId: Any): List<Any> = throw UnsupportedOperationException()

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.DOC.TENANT_ID
			"accountId" -> Tables.DOC.ACCOUNT_ID
			"ownerId" -> Tables.DOC.OWNER_ID
			"assigneeId" -> Tables.DOC.ASSIGNEE_ID
			else -> return null
		}
		return dslContext
			.select(Tables.DOC.ID)
			.from(Tables.DOC)
			.where(field.eq(targetId as Int))
			.and(Tables.DOC.DOC_TYPE_ID.eq(aggregateTypeId))
			.fetch(Tables.DOC.ID)
	}

}

