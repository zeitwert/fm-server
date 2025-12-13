package io.zeitwert.dddrive.ddd.persist.jooq

import io.dddrive.core.doc.model.Doc
import io.dddrive.core.doc.model.enums.CodeCaseDef
import io.dddrive.core.doc.model.enums.CodeCaseDefEnum
import io.dddrive.core.doc.model.enums.CodeCaseStage
import io.dddrive.core.doc.model.enums.CodeCaseStageEnum
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.doc.model.db.Tables
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord
import org.jooq.UpdatableRecord
import java.time.OffsetDateTime

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
		record.id = aggregate.getProperty("id")?.let { (it as BaseProperty<Int?>).value }
		record.docTypeId = getAggregateTypeId()
		record.tenantId = (aggregate.getProperty("tenant") as? ReferenceProperty<*>)?.id as? Int
		record.version = (aggregate.getProperty("version") as? BaseProperty<Int?>)?.value ?: 0
		record.ownerId = (aggregate.getProperty("owner") as? ReferenceProperty<*>)?.id as? Int
		record.caption = (aggregate.getProperty("caption") as? BaseProperty<String?>)?.value
		record.createdByUserId = (aggregate.getProperty("createdByUser") as? ReferenceProperty<*>)?.id as? Int
		record.createdAt = (aggregate.getProperty("createdAt") as? BaseProperty<OffsetDateTime?>)?.value
		record.modifiedByUserId = (aggregate.getProperty("modifiedByUser") as? ReferenceProperty<*>)?.id as? Int
		record.modifiedAt = (aggregate.getProperty("modifiedAt") as? BaseProperty<OffsetDateTime?>)?.value

		// Doc-specific fields
		val caseDef = (aggregate.getProperty("caseDef") as? EnumProperty<CodeCaseDef>)?.value
		record.caseDefId = caseDef?.id ?: getDefaultCaseDefId()

		val caseStage = (aggregate.getProperty("caseStage") as? EnumProperty<CodeCaseStage>)?.value
		record.caseStageId = caseStage?.id ?: getDefaultCaseStageId()

		record.isInWork = (aggregate.getProperty("isInWork") as? BaseProperty<Boolean?>)?.value
		record.assigneeId = (aggregate.getProperty("assignee") as? ReferenceProperty<*>)?.id as? Int

		// Account ID (FM-specific, may be null for some doc types)
		if (aggregate.hasProperty("accountId")) {
			record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		}

		return record
	}

	/**
	 * Loads the DOC record from the database.
	 */
	override fun loadRecord(id: Int): DocRecord? {
		return dslContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(id))
	}

	/**
	 * Maps DOC record fields to the aggregate, including Doc-specific fields.
	 */
	@Suppress("UNCHECKED_CAST")
	override fun toAggregate(record: UpdatableRecord<*>, aggregate: D) {
		super.toAggregate(record, aggregate)

		val docRecord = record as DocRecord

		// Doc-specific fields
		(aggregate.getProperty("docTypeId") as? BaseProperty<String?>)?.value = docRecord.docTypeId

		// Case definition
		docRecord.caseDefId?.let { caseDefId ->
			val caseDef = CodeCaseDefEnum.getCaseDef(caseDefId)
			(aggregate.getProperty("caseDef") as? EnumProperty<CodeCaseDef>)?.value = caseDef
		}

		// Case stage
		docRecord.caseStageId?.let { caseStageId ->
			val caseStage = CodeCaseStageEnum.getCaseStage(caseStageId)
			(aggregate.getProperty("caseStage") as? EnumProperty<CodeCaseStage>)?.value = caseStage
		}

		(aggregate.getProperty("isInWork") as? BaseProperty<Boolean?>)?.value = docRecord.isInWork
		(aggregate.getProperty("assignee") as? ReferenceProperty<ObjUser>)?.id = docRecord.assigneeId

		// Account ID (FM-specific)
		if (aggregate.hasProperty("accountId")) {
			(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = docRecord.accountId
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
	protected open fun loadTransitions(aggregate: D, docId: Int?) {
		// Default: no transitions to load
		// Subclasses should load from doc_part_transition table
	}

	/**
	 * Loads extension data for the aggregate.
	 * Override in subclasses to load from extension tables.
	 */
	protected open fun loadExtension(aggregate: D, docId: Int?) {
		// Default: no extension data to load
	}

	override fun getAllRecordIds(tenantId: Int): List<Int> {
		return dslContext()
			.select(Tables.DOC.ID)
			.from(Tables.DOC)
			.where(Tables.DOC.TENANT_ID.eq(tenantId))
			.and(Tables.DOC.DOC_TYPE_ID.eq(getAggregateTypeId()))
			.fetch(Tables.DOC.ID)
	}

	override fun getRecordIdsByForeignKey(fkName: String, targetId: Int): List<Int> {
		// Handle common foreign keys
		val field = when (fkName) {
			"accountId" -> Tables.DOC.ACCOUNT_ID
			"ownerId" -> Tables.DOC.OWNER_ID
			"tenantId" -> Tables.DOC.TENANT_ID
			"assigneeId" -> Tables.DOC.ASSIGNEE_ID
			else -> return emptyList() // Unknown FK - subclasses may override for custom FKs
		}

		return dslContext()
			.select(Tables.DOC.ID)
			.from(Tables.DOC)
			.where(field.eq(targetId))
			.and(Tables.DOC.DOC_TYPE_ID.eq(getAggregateTypeId()))
			.fetch(Tables.DOC.ID)
	}
}

