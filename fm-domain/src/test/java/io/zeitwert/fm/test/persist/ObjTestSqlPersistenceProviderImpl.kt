package io.zeitwert.fm.test.persist

import dddrive.ddd.path.setValueByPath
import dddrive.ddd.property.model.PartListProperty
import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.app.model.RequestContextFM
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjPartItemSqlPersistenceProviderImpl
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.db.Tables
import io.zeitwert.fm.test.model.db.tables.records.ObjTestRecord
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.stereotype.Component

@Component("objTestPersistenceProvider")
open class ObjTestSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val requestCtx: RequestContextFM,
) : FMObjSqlPersistenceProviderBase<ObjTest>(ObjTest::class.java),
	SqlRecordMapper<ObjTest> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjTest) {
		val record = dslContext.fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(aggregate.id as Int))
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
	override fun doLoadParts(aggregate: ObjTest) {
		super.doLoadParts(aggregate)
		ObjTestPartNodeSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginLoad()
			loadPartList(
				aggregate.getProperty("nodeList", ObjTestPartNode::class) as PartListProperty<ObjTest, ObjTestPartNode>,
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
	override fun doStoreParts(aggregate: ObjTest) {
		super.doStoreParts(aggregate)
		ObjTestPartNodeSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			storePartList(
				aggregate.getProperty("nodeList", ObjTestPartNode::class) as PartListProperty<ObjTest, ObjTestPartNode>,
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

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_TEST.OBJ_ID)
			.from(Tables.OBJ_TEST)
			.where(Tables.OBJ_TEST.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_TEST.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"refTestId" -> Tables.OBJ_TEST.REF_TEST_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_TEST.OBJ_ID)
			.from(Tables.OBJ_TEST)
			.where(field.eq(targetId as Int))
			// .and(Tables.OBJ_TEST.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_TEST.OBJ_ID)
	}

}
