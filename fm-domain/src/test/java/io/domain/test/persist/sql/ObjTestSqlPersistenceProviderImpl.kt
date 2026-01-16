package io.domain.test.persist.sql

import dddrive.property.model.EntityWithProperties
import dddrive.property.model.PartListProperty
import dddrive.property.path.setValueByPath
import dddrive.query.QuerySpec
import io.domain.test.model.ObjTest
import io.domain.test.model.ObjTestPartNode
import io.domain.test.model.db.Tables
import io.domain.test.model.db.tables.records.ObjTestRecord
import io.domain.test.model.enums.CodeTestType
import io.domain.test.persist.ObjTestPersistenceProvider
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.obj.base.ObjSqlPersistenceProviderBase
import io.zeitwert.persist.sql.obj.impl.ObjPartItemSqlPersistenceProviderImpl
import io.zeitwert.persist.sql.obj.impl.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component("objTestPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "sql", matchIfMissing = true)
open class ObjTestSqlPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
	private val dslContextProvider: ObjectProvider<DSLContext>,
) : ObjSqlPersistenceProviderBase<ObjTest>(ObjTest::class.java),
	SqlRecordMapper<ObjTest>,
	ObjTestPersistenceProvider {

	override val dslContext: DSLContext
		get() = dslContextProvider.getObject()

	override val idProvider: SqlIdProvider
		get() = baseRecordMapper

	override val baseRecordMapper: ObjRecordMapperImpl
		get() = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper
		get() = this

	override fun loadRecord(aggregate: ObjTest) {
		val record =
			dslContext.fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_TEST record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjTest,
		record: ObjTestRecord,
	) {
		aggregate.shortText = record.shortText
		aggregate.longText = record.longText
		aggregate.date = record.date
		aggregate.int = record.int
		aggregate.isDone = record.isDone
		aggregate.json = record.json?.toString()
		aggregate.nr = record.nr
		aggregate.setValueByPath("refObjId", record.refTestId)
		aggregate.testType = CodeTestType.getTestType(record.testTypeId)
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadParts(aggregate: ObjTest) {
		super.loadParts(aggregate)
		ObjTestPartNodeSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginLoad()
			loadPartList(
				(aggregate as EntityWithProperties).getProperty(
					"nodeList",
					ObjTestPartNode::class,
				) as
					PartListProperty<ObjTest, ObjTestPartNode>,
				"test.nodeList",
			)
			endLoad()
		}
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginLoad()
			items("test.testTypeSet").forEach {
				aggregate.testTypeSet.add(CodeTestType.getTestType(it)!!)
			}
			endLoad()
		}
	}

	override fun storeRecord(aggregate: ObjTest) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjTest): ObjTestRecord {
		val record = dslContext.newRecord(Tables.OBJ_TEST)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		if (aggregate is ItemWithAccount) {
			record.accountId = aggregate.accountId as Int?
		}
		record.shortText = aggregate.shortText
		record.longText = aggregate.longText
		record.date = aggregate.date
		record.int = aggregate.int
		record.isDone = aggregate.isDone
		record.json = JSON.valueOf(aggregate.json)
		record.nr = aggregate.nr
		record.refTestId = aggregate.refObjId as? Int
		record.testTypeId = aggregate.testType?.id
		return record
	}

	@Suppress("UNCHECKED_CAST")
	override fun storeParts(aggregate: ObjTest) {
		super.storeParts(aggregate)
		ObjTestPartNodeSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			storePartList(
				(aggregate as EntityWithProperties).getProperty(
					"nodeList",
					ObjTestPartNode::class,
				) as
					PartListProperty<ObjTest, ObjTestPartNode>,
				"test.nodeList",
			)
			endStore()
		}
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			addItems("test.testTypeSet", aggregate.testTypeSet.map { it.id })
			endStore()
		}
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_TEST_V, Tables.OBJ_TEST_V.ID, query)

}
