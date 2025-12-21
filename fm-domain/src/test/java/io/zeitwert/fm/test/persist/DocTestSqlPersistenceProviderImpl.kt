package io.zeitwert.fm.test.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.doc.model.Doc
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.dddrive.persist.base.SqlAggregatePersistenceProviderBase
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord
import io.zeitwert.fm.doc.persist.DocRecordMapperImpl
import io.zeitwert.fm.test.model.DocTest
import io.zeitwert.fm.test.model.db.Tables
import io.zeitwert.fm.test.model.db.tables.records.DocTestRecord
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.stereotype.Component

/**
 * jOOQ-based persistence provider for DocTest aggregates.
 */
@Component("docTestPersistenceProvider")
open class DocTestPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<DocTest, DocRecord, DocTestRecord>(DocTest::class.java),
	SqlRecordMapper<DocTest, DocTestRecord> {

	override val idProvider: SqlIdProvider<Doc> get() = baseRecordMapper

	override val baseRecordMapper = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): DocTestRecord {
		val record = dslContext.fetchOne(Tables.DOC_TEST, Tables.DOC_TEST.DOC_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no DOC_TEST record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: DocTest,
		record: DocTestRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.setValueByPath("shortText", record.shortText)
		aggregate.setValueByPath("longText", record.longText)
		aggregate.setValueByPath("date", record.date)
		aggregate.setValueByPath("int", record.int)
		aggregate.setValueByPath("isDone", record.isDone)
		aggregate.setValueByPath("json", record.json?.toString())
		aggregate.setValueByPath("nr", record.nr)
		aggregate.setValueByPath("refObjId", record.refObjId)
		aggregate.setValueByPath("refDocId", record.refDocId)
		record.testTypeId?.let { testTypeId ->
			aggregate.setValueByPath("testType", CodeTestType.Enumeration.getTestType(testTypeId))
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: DocTest): DocTestRecord {
		val record = dslContext.newRecord(Tables.DOC_TEST)

		record.docId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.shortText = aggregate.shortText
		record.longText = aggregate.longText
		record.date = aggregate.date
		record.int = aggregate.int
		record.isDone = aggregate.isDone
		record.json = aggregate.json?.let { JSON.valueOf(it) }
		record.nr = aggregate.nr
		record.testTypeId = aggregate.testType?.id
		record.refObjId = aggregate.refObjId
		record.refDocId = aggregate.refDocId

		return record
	}

	override fun storeRecord(
		record: DocTestRecord,
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
			.select(Tables.DOC_TEST.DOC_ID)
			.from(Tables.DOC_TEST)
			.where(Tables.DOC_TEST.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.DOC_TEST.DOC_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.DOC_TEST.TENANT_ID
			"accountId" -> Tables.DOC_TEST.ACCOUNT_ID
			"refObjId" -> Tables.DOC_TEST.REF_OBJ_ID
			"refDocId" -> Tables.DOC_TEST.REF_DOC_ID
			else -> return null
		}
		return dslContext
			.select(Tables.DOC_TEST.DOC_ID)
			.from(Tables.DOC_TEST)
			.where(field.eq(targetId as Int))
			.fetch(Tables.DOC_TEST.DOC_ID)
	}

}
