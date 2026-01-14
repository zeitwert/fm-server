package io.domain.test.persist.sql

import dddrive.property.path.setValueByPath
import dddrive.query.QuerySpec
import io.domain.test.model.DocTest
import io.domain.test.model.db.Tables
import io.domain.test.model.db.tables.records.DocTestRecord
import io.domain.test.model.enums.CodeTestType
import io.domain.test.persist.DocTestPersistenceProvider
import io.zeitwert.app.doc.model.base.FMDocBase
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.doc.base.DocSqlPersistenceProviderBase
import io.zeitwert.persist.sql.doc.impl.DocPartItemSqlPersistenceProviderImpl
import io.zeitwert.persist.sql.doc.impl.DocRecordMapperImpl
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/** jOOQ-based persistence provider for DocTest aggregates. */
@Component("docTestPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class DocTestSqlPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
	private val dslContextProvider: ObjectProvider<DSLContext>,
) : DocSqlPersistenceProviderBase<DocTest>(DocTest::class.java),
	SqlRecordMapper<DocTest>,
	DocTestPersistenceProvider {

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

	override fun loadParts(aggregate: DocTest) {
		super.loadParts(aggregate)
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

	override fun storeParts(aggregate: DocTest) {
		super.storeParts(aggregate)
		DocPartItemSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			addItems("test.testTypeSet", aggregate.testTypeSet.map { it.id })
			endStore()
		}
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.DOC_TEST_V, Tables.DOC_TEST_V.ID, query)

}
