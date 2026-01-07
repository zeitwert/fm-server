package io.zeitwert.domain.test.persist

import dddrive.ddd.path.setValueByPath
import dddrive.ddd.query.QuerySpec
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.domain.test.model.DocTest
import io.zeitwert.domain.test.model.db.Tables
import io.zeitwert.domain.test.model.db.tables.records.DocTestRecord
import io.zeitwert.domain.test.model.enums.CodeTestType
import io.zeitwert.fm.doc.model.base.FMDocBase
import io.zeitwert.fm.doc.persist.DocPartItemSqlPersistenceProviderImpl
import io.zeitwert.fm.doc.persist.DocRecordMapperImpl
import io.zeitwert.fm.doc.persist.FMDocSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

/** jOOQ-based persistence provider for DocTest aggregates. */
@Component("docTestPersistenceProvider")
open class DocTestSqlPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	private val dslContextProvider: ObjectProvider<DSLContext>,
) : FMDocSqlPersistenceProviderBase<DocTest>(DocTest::class.java),
	SqlRecordMapper<DocTest> {

	override val dslContext: DSLContext
		get() = dslContextProvider.getObject()

	override val idProvider: SqlIdProvider
		get() = baseRecordMapper

	override val baseRecordMapper: DocRecordMapperImpl
		get() = DocRecordMapperImpl(dslContext)

	override val extnRecordMapper
		get() = this

	override fun loadRecord(aggregate: DocTest) {
		val record =
			dslContext.fetchOne(Tables.DOC_TEST, Tables.DOC_TEST.DOC_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no DOC_TEST record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: DocTest,
		record: DocTestRecord,
	) {
		aggregate.accountId = record.accountId
		aggregate.shortText = record.shortText
		aggregate.longText = record.longText
		aggregate.date = record.date
		aggregate.int = record.int
		aggregate.isDone = record.isDone
		aggregate.json = record.json?.toString()
		aggregate.nr = record.nr
		aggregate.setValueByPath("refObjId", record.refObjId)
		aggregate.setValueByPath("refDocId", record.refDocId)
		aggregate.testType = CodeTestType.getTestType(record.testTypeId)
	}

	override fun doLoadParts(aggregate: DocTest) {
		super.doLoadParts(aggregate)
		DocPartItemSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginLoad()
			items("test.testTypeSet").forEach {
				aggregate.testTypeSet.add(CodeTestType.getTestType(it)!!)
			}
			endLoad()
		}
	}

	override fun storeRecord(aggregate: DocTest) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMDocBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: DocTest): DocTestRecord {
		val record = dslContext.newRecord(Tables.DOC_TEST)

		record.docId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as Int

		record.shortText = aggregate.shortText
		record.longText = aggregate.longText
		record.date = aggregate.date
		record.int = aggregate.int
		record.isDone = aggregate.isDone
		record.json = aggregate.json?.let { JSON.valueOf(it) }
		record.nr = aggregate.nr
		record.testTypeId = aggregate.testType?.id
		record.refObjId = aggregate.refObjId as? Int
		record.refDocId = aggregate.refDocId as? Int

		return record
	}

	override fun doStoreParts(aggregate: DocTest) {
		super.doStoreParts(aggregate)
		DocPartItemSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			addItems("test.testTypeSet", aggregate.testTypeSet.map { it.id })
			endStore()
		}
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC_TEST_V, Tables.DOC_TEST_V.ID, query)

}
