package io.zeitwert.persist.sql.doc.impl

import dddrive.query.QuerySpec
import io.zeitwert.app.doc.model.base.FMDocBase
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.db.Tables
import io.zeitwert.fm.task.model.db.tables.records.DocTaskRecord
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import io.zeitwert.persist.DocTaskPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.doc.base.DocSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component("docTaskPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "sql", matchIfMissing = true)
open class DocTaskSqlPersistenceProvider(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : DocSqlPersistenceProviderBase<DocTask>(DocTask::class.java),
	SqlRecordMapper<DocTask>,
	DocTaskPersistenceProvider {

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
		aggregate.relatedToId = record.relatedToId
		aggregate.subject = record.subject
		aggregate.content = record.content
		aggregate.isPrivate = record.isPrivate
		aggregate.dueAt = record.dueAt
		aggregate.remindAt = record.remindAt
		record.priorityId?.let { priorityId ->
			aggregate.priority = CodeTaskPriority.Enumeration.getPriority(priorityId)
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
		record.relatedToId = aggregate.relatedToId as? Int
		record.subject = aggregate.subject
		record.content = aggregate.content
		record.isPrivate = aggregate.isPrivate
		record.priorityId = aggregate.priority?.id
		record.dueAt = aggregate.dueAt
		record.remindAt = aggregate.remindAt

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC_TASK_V, Tables.DOC_TASK_V.ID, query)

}
