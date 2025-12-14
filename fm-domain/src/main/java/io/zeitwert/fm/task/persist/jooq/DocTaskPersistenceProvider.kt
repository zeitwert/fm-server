package io.zeitwert.fm.task.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqDocPersistenceProviderBase
import io.zeitwert.fm.doc.model.db.Sequences
import io.zeitwert.fm.task.model.DocTask
import io.zeitwert.fm.task.model.DocTaskRepository
import io.zeitwert.fm.task.model.db.Tables
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component("docTaskPersistenceProvider")
open class DocTaskPersistenceProvider : JooqDocPersistenceProviderBase<DocTask>() {

	private lateinit var _dslContext: DSLContext
	private lateinit var _repository: DocTaskRepository

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	@Autowired
	@Lazy
	fun setRepository(repository: DocTaskRepository) {
		this._repository = repository
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun getDefaultCaseDefId(): String = "task"

	override fun getDefaultCaseStageId(): String = "task.new"

	override fun nextAggregateId(): Any =
		dslContext()
			.nextval(Sequences.DOC_ID_SEQ)
			.toInt()

	override fun fromAggregate(aggregate: DocTask): UpdatableRecord<*> = createDocRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: DocTask) {
		val docId = aggregate.id as Int

		val existingRecord = dslContext().fetchOne(
			Tables.DOC_TASK,
			Tables.DOC_TASK.DOC_ID.eq(docId),
		)

		val record = existingRecord ?: dslContext().newRecord(Tables.DOC_TASK)

		record.docId = docId
		record.tenantId = aggregate.tenantId as? Int
		record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		record.relatedObjId = (aggregate.getProperty("relatedObjId") as? BaseProperty<Int?>)?.value
		record.relatedDocId = (aggregate.getProperty("relatedDocId") as? BaseProperty<Int?>)?.value
		record.subject = (aggregate.getProperty("subject") as? BaseProperty<String?>)?.value
		record.content = (aggregate.getProperty("content") as? BaseProperty<String?>)?.value
		record.isPrivate = (aggregate.getProperty("isPrivate") as? BaseProperty<Boolean?>)?.value
		record.priorityId = (aggregate.getProperty("priority") as? EnumProperty<CodeTaskPriority>)?.value?.id
		record.dueAt = (aggregate.getProperty("dueAt") as? BaseProperty<OffsetDateTime?>)?.value
		record.remindAt = (aggregate.getProperty("remindAt") as? BaseProperty<OffsetDateTime?>)?.value

		if (existingRecord != null) {
			record.update()
		} else {
			record.insert()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: DocTask,
		docId: Int?,
	) {
		if (docId == null) return

		val record = dslContext().fetchOne(
			Tables.DOC_TASK,
			Tables.DOC_TASK.DOC_ID.eq(docId),
		) ?: return

		(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.accountId
		(aggregate.getProperty("relatedObjId") as? BaseProperty<Int?>)?.value = record.relatedObjId
		(aggregate.getProperty("relatedDocId") as? BaseProperty<Int?>)?.value = record.relatedDocId
		(aggregate.getProperty("subject") as? BaseProperty<String?>)?.value = record.subject
		(aggregate.getProperty("content") as? BaseProperty<String?>)?.value = record.content
		(aggregate.getProperty("isPrivate") as? BaseProperty<Boolean?>)?.value = record.isPrivate
		(aggregate.getProperty("dueAt") as? BaseProperty<OffsetDateTime?>)?.value = record.dueAt
		(aggregate.getProperty("remindAt") as? BaseProperty<OffsetDateTime?>)?.value = record.remindAt

		record.priorityId?.let { priorityId ->
			(aggregate.getProperty("priority") as? EnumProperty<CodeTaskPriority>)?.value =
				CodeTaskPriority.getPriority(priorityId)
		}
	}

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> {
		val field = when (fkName) {
			"relatedObjId" -> Tables.DOC_TASK.RELATED_OBJ_ID
			"relatedDocId" -> Tables.DOC_TASK.RELATED_DOC_ID
			else -> null
		}
		if (field != null) {
			return dslContext()
				.select(Tables.DOC_TASK.DOC_ID)
				.from(Tables.DOC_TASK)
				.where(field.eq(targetId as Int))
				.fetch(Tables.DOC_TASK.DOC_ID)
		}
		return super.getByForeignKey(fkName, targetId)
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "doc_task"
	}

}
