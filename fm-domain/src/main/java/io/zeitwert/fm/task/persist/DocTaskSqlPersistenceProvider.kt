package io.zeitwert.fm.task.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.doc.persist.DocRecordMapperImpl
import io.zeitwert.fm.doc.persist.FMDocSqlPersistenceProviderBase
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.db.Tables
import io.zeitwert.fm.task.model.db.tables.records.DocTaskRecord
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("docTaskPersistenceProvider")
open class DocTaskSqlPersistenceProvider(
	override val dslContext: DSLContext,
	override val requestCtx: RequestContextFM,
) : FMDocSqlPersistenceProviderBase<DocTask>(DocTask::class.java),
	SqlRecordMapper<DocTask> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: DocTask) {
		val record = dslContext.fetchOne(Tables.DOC_TASK, Tables.DOC_TASK.DOC_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no DOC_TASK record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: DocTask,
		record: DocTaskRecord,
	) {
		aggregate.accountId = record.accountId
		aggregate.setValueByPath("relatedObjId", record.relatedObjId)
		aggregate.setValueByPath("relatedDocId", record.relatedDocId)
		aggregate.subject = record.subject
		aggregate.content = record.content
		aggregate.isPrivate = record.isPrivate
		aggregate.dueAt = record.dueAt
		aggregate.remindAt = record.remindAt
		record.priorityId?.let { priorityId ->
			aggregate.setValueByPath("priority", CodeTaskPriority.Enumeration.getPriority(priorityId))
		}
	}

	override fun storeRecord(aggregate: DocTask) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMDocBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: DocTask): DocTaskRecord {
		val record = dslContext.newRecord(Tables.DOC_TASK)

		record.docId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as Int

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

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC_TASK_V, Tables.DOC_TASK_V.ID, query)

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
