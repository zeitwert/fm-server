package io.zeitwert.fm.test.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.db.Tables
import io.zeitwert.fm.test.model.db.tables.records.ObjTestRecord
import io.zeitwert.fm.test.model.enums.CodeTestType
import org.jooq.DSLContext
import org.jooq.JSON
import org.springframework.stereotype.Component

@Component("objTestPersistenceProvider")
open class ObjTestPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjTest, ObjRecord, ObjTestRecord>(ObjTest::class.java),
	SqlAggregateRecordMapper<ObjTest, ObjTestRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjTestRecord {
		val record = dslContext.fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_TEST record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjTest,
		record: ObjTestRecord,
	) {
		aggregate.setValueByPath("shortText", record.shortText)
		aggregate.setValueByPath("longText", record.longText)
		aggregate.setValueByPath("date", record.date)
		aggregate.setValueByPath("int", record.int)
		aggregate.setValueByPath("isDone", record.isDone)
		aggregate.setValueByPath("json", record.json?.toString())
		aggregate.setValueByPath("nr", record.nr)
		aggregate.setValueByPath("refTestId", record.refTestId)
		aggregate.setValueByPath("testType", CodeTestType.getTestType(record.testTypeId))
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjTest): ObjTestRecord {
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
		record.refTestId = aggregate.refTest?.id as? Int
		record.testTypeId = aggregate.testType?.id
		return record
	}

	override fun storeRecord(
		record: ObjTestRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

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
