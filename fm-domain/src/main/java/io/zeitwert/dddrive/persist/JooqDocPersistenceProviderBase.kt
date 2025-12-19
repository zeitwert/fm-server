package io.zeitwert.dddrive.persist

import io.dddrive.core.doc.model.Doc
import io.dddrive.path.getValueByPath
import io.dddrive.path.setValueByPath
import io.zeitwert.fm.doc.model.db.Tables
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord
import org.jooq.UpdatableRecord

/**
 * Base persistence provider for Doc aggregates using jOOQ.
 *
 * This class extends JooqAggregatePersistenceProviderBase to add Doc-specific
 * field handling (docTypeId, caseDef, caseStage, assignee, isInWork) and works
 * with the DOC table plus any extension tables defined in subclasses.
 *
 * @param D The Doc aggregate type
 */
abstract class JooqDocPersistenceProviderBase<D : Doc> : JooqAggregatePersistenceProviderBase<D>() {

	/**
	 * Returns the aggregate type ID for this Doc type (e.g., "doc_task").
	 */
	protected abstract fun getAggregateTypeId(): String

	/**
	 * Returns the default case definition ID for new documents.
	 */
	protected abstract fun getDefaultCaseDefId(): String

	/**
	 * Returns the default case stage ID for new documents.
	 */
	protected abstract fun getDefaultCaseStageId(): String

	/**
	 * Creates a new DocRecord populated from the aggregate.
	 */
	@Suppress("UNCHECKED_CAST")
	protected fun createDocRecord(aggregate: D): DocRecord {
		val record = dslContext().newRecord(Tables.DOC)

		// Set base fields
		record.id = aggregate.getValueByPath("id")
		record.docTypeId = getAggregateTypeId()
		record.tenantId = aggregate.getValueByPath("tenantId")
		record.version = aggregate.getValueByPath("version") ?: 0
		record.ownerId = aggregate.getValueByPath("owner.id")
		record.caption = aggregate.getValueByPath("caption")
		record.createdByUserId = aggregate.getValueByPath("createdByUser.id")
		record.createdAt = aggregate.getValueByPath("createdAt")
		record.modifiedByUserId = aggregate.getValueByPath("modifiedByUser.id")
		record.modifiedAt = aggregate.getValueByPath("modifiedAt")

		// Doc-specific fields
		record.caseDefId = aggregate.getValueByPath("caseDefId") ?: getDefaultCaseDefId()
		record.caseStageId = aggregate.getValueByPath("caseStageId") ?: getDefaultCaseStageId()
		record.isInWork = aggregate.getValueByPath("isInWork")
		record.assigneeId = aggregate.getValueByPath("assigneeId")

		// Account ID (FM-specific, may be null for some doc types)
		if (aggregate.hasProperty("accountId")) {
			record.accountId = aggregate.getValueByPath("accountId")
		}

		return record
	}

	/**
	 * Loads the DOC record from the database.
	 */
	override fun loadRecord(id: Int): DocRecord? = dslContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(id))

	/**
	 * Maps DOC record fields to the aggregate, including Doc-specific fields.
	 */
	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(
		record: UpdatableRecord<*>,
		aggregate: D,
	) {
		super.toAggregate(record, aggregate)

		val docRecord = record as DocRecord

		aggregate.setValueByPath("docTypeId", docRecord.docTypeId)
		aggregate.setValueByPath("caseDefId", docRecord.caseDefId)
		aggregate.setValueByPath("caseStageId", docRecord.caseStageId)
		aggregate.setValueByPath("isInWork", docRecord.isInWork)
		aggregate.setValueByPath("assigneeId", docRecord.assigneeId)

		if (aggregate.hasProperty("accountId")) {
			aggregate.setValueByPath("accountId", docRecord.accountId)
		}

		// Load maxPartId - this may need to be tracked differently
		loadMaxPartId(aggregate)

		// Load transitions
		loadTransitions(aggregate, docRecord.id)

		// Load extension data if present
		loadExtension(aggregate, docRecord.id)
	}

	/**
	 * Loads or calculates the maxPartId for the aggregate.
	 * Override in subclasses if needed.
	 */
	protected open fun loadMaxPartId(aggregate: D) {
		// Default implementation - subclasses may need to track this differently
	}

	/**
	 * Loads transition history for the document.
	 * Override in subclasses to load from transition tables.
	 */
	protected open fun loadTransitions(
		aggregate: D,
		docId: Int?,
	) {
		// Default: no transitions to load
		// Subclasses should load from doc_part_transition table
	}

	/**
	 * Loads extension data for the aggregate.
	 * Override in subclasses to load from extension tables.
	 */
	protected open fun loadExtension(
		aggregate: D,
		docId: Int?,
	) {
		// Default: no extension data to load
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext()
			.select(Tables.DOC.ID)
			.from(Tables.DOC)
			.where(Tables.DOC.TENANT_ID.eq(tenantId as Int))
			.and(Tables.DOC.DOC_TYPE_ID.eq(getAggregateTypeId()))
			.fetch(Tables.DOC.ID)

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> {
		val field = when (fkName) {
			"tenantId" -> Tables.DOC.TENANT_ID
			"accountId" -> Tables.DOC.ACCOUNT_ID
			"ownerId" -> Tables.DOC.OWNER_ID
			"assigneeId" -> Tables.DOC.ASSIGNEE_ID
			else -> throw IllegalArgumentException("unknown fkName: $fkName")
		}
		return dslContext()
			.select(Tables.DOC.ID)
			.from(Tables.DOC)
			.where(field.eq(targetId as Int))
			.and(Tables.DOC.DOC_TYPE_ID.eq(getAggregateTypeId()))
			.fetch(Tables.DOC.ID)
	}

}
