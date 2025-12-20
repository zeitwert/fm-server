package io.zeitwert.fm.task.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.doc.model.Doc
import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord
import io.zeitwert.fm.doc.persist.DocRecordMapperImpl
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.db.Tables
import io.zeitwert.fm.task.model.db.tables.records.DocTaskRecord
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("docTaskPersistenceProvider")
open class DocTaskPersistenceProvider(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<DocTask, DocRecord, DocTaskRecord>(DocTask::class.java),
	SqlAggregateRecordMapper<DocTask, DocTaskRecord> {

	override val idProvider: SqlIdProvider<Doc> get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): DocTaskRecord {
		val record = dslContext.fetchOne(Tables.DOC_TASK, Tables.DOC_TASK.DOC_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no DOC_TASK record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: DocTask,
		record: DocTaskRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.setValueByPath("relatedObjId", record.relatedObjId)
		aggregate.setValueByPath("relatedDocId", record.relatedDocId)
		aggregate.setValueByPath("subject", record.subject)
		aggregate.setValueByPath("content", record.content)
		aggregate.setValueByPath("isPrivate", record.isPrivate)
		aggregate.setValueByPath("dueAt", record.dueAt)
		aggregate.setValueByPath("remindAt", record.remindAt)
		record.priorityId?.let { priorityId ->
			aggregate.setValueByPath("priority", CodeTaskPriority.getPriority(priorityId))
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: DocTask): DocTaskRecord {
		val record = dslContext.newRecord(Tables.DOC_TASK)

		record.docId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.relatedObjId = aggregate.getValueByPath("relatedObjId") as Int?
		record.relatedDocId = aggregate.getValueByPath("relatedDocId") as Int?
		record.subject = aggregate.subject
		record.content = aggregate.content
		record.isPrivate = aggregate.isPrivate
		record.priorityId = aggregate.priority?.id
		record.dueAt = aggregate.dueAt
		record.remindAt = aggregate.remindAt

		return record
	}

	override fun storeRecord(
		record: DocTaskRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMDocBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.DOC_TASK.DOC_ID)
			.from(Tables.DOC_TASK)
			.where(Tables.DOC_TASK.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.DOC_TASK.DOC_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.DOC_TASK.TENANT_ID
			"accountId" -> Tables.DOC_TASK.ACCOUNT_ID
			"relatedObjId" -> Tables.DOC_TASK.RELATED_OBJ_ID
			"relatedDocId" -> Tables.DOC_TASK.RELATED_DOC_ID
			else -> return null
		}
		return dslContext
			.select(Tables.DOC_TASK.DOC_ID)
			.from(Tables.DOC_TASK)
			.where(field.eq(targetId as Int))
			.fetch(Tables.DOC_TASK.DOC_ID)
	}

}
