package io.zeitwert.fm.account.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.db.Tables
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objAccountPersistenceProvider")
open class ObjAccountPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjAccount, ObjRecord, ObjAccountRecord>(ObjAccount::class.java),
	SqlAggregateRecordMapper<ObjAccount, ObjAccountRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjAccountRecord {
		val record = dslContext.fetchOne(Tables.OBJ_ACCOUNT, Tables.OBJ_ACCOUNT.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_ACCOUNT record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjAccount,
		record: ObjAccountRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)

		aggregate.setValueByPath("name", record.name)
		aggregate.setValueByPath("description", record.description)
		aggregate.setValueByPath("accountTypeId", record.accountTypeId)
		aggregate.setValueByPath("clientSegmentId", record.clientSegmentId)
		aggregate.setValueByPath("referenceCurrencyId", record.referenceCurrencyId)
		aggregate.setValueByPath("inflationRate", record.inflationRate)
		aggregate.setValueByPath("discountRate", record.discountRate)
		aggregate.setValueByPath("logoImageId", record.logoImgId)
		aggregate.setValueByPath("mainContactId", record.mainContactId)
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjAccount): ObjAccountRecord {
		val record = dslContext.newRecord(Tables.OBJ_ACCOUNT)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.id as Int

		record.name = aggregate.name
		record.description = aggregate.description
		record.accountTypeId = aggregate.accountType?.id
		record.clientSegmentId = aggregate.clientSegment?.id
		record.referenceCurrencyId = aggregate.referenceCurrency?.id
		record.inflationRate = aggregate.inflationRate
		record.discountRate = aggregate.discountRate
		record.logoImgId = aggregate.logoImageId
		record.mainContactId = aggregate.mainContactId

		return record
	}

	override fun storeRecord(
		record: ObjAccountRecord,
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
			.select(Tables.OBJ_ACCOUNT.OBJ_ID)
			.from(Tables.OBJ_ACCOUNT)
			.where(Tables.OBJ_ACCOUNT.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_ACCOUNT.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ_ACCOUNT.TENANT_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_ACCOUNT.OBJ_ID)
			.from(Tables.OBJ_ACCOUNT)
			.where(field.eq(targetId as Int))
			// .and(Tables.OBJ_TEST.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_ACCOUNT.OBJ_ID)
	}

}
