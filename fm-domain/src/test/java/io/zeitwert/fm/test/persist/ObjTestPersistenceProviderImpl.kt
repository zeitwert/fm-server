package io.zeitwert.fm.test.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.property.model.BaseProperty
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.Sequences
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
	override val baseRecordMapper: ObjRecordMapperImpl,
	val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjTest, ObjRecord, ObjTestRecord>(ObjTest::class.java),
	SqlAggregateRecordMapper<ObjTest, ObjTestRecord> {

	override fun dslContext(): DSLContext = dslContext

	override val extnRecordMapper get() = this

	override fun nextId(): Any = dslContext.nextval(Sequences.OBJ_ID_SEQ).toInt()

	override fun loadRecord(aggregateId: Any): ObjTestRecord {
		val record = dslContext.fetchOne(Tables.OBJ_TEST, Tables.OBJ_TEST.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_TEST record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjTest,
		record: ObjTestRecord,
	) {
		check(aggregate.id != null) { "id defined" }
		check(aggregate.meta.objTypeId != null) { "objTypeId defined" }
		// obj_test
		aggregate.setValueByPath("shortText", record.shortText)
		aggregate.setValueByPath("longText", record.longText)
		aggregate.setValueByPath("date", record.date)
		aggregate.setValueByPath("int", record.int)
		aggregate.setValueByPath("isDone", record.isDone)
		aggregate.setValueByPath("json", record.json?.toString())
		aggregate.setValueByPath("nr", record.nr)
		aggregate.setValueByPath("refTest.id", record.refTestId)
		aggregate.setValueByPath("testType", CodeTestType.getTestType(record.testTypeId))
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjTest): ObjTestRecord {
		val record = dslContext.newRecord(Tables.OBJ_TEST)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		if (aggregate.hasProperty("accountId")) {
			record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
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

	override fun getAll(tenantId: Any): List<Any> = emptyList()

	override fun getByForeignKey(
		fkName: String,
		targetId: Any,
	): List<Any> = emptyList()

}
