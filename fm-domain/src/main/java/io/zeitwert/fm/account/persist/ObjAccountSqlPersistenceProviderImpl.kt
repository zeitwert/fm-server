package io.zeitwert.fm.account.persist

import dddrive.ddd.path.setValueByPath
import dddrive.ddd.query.QuerySpec
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.obj.model.base.FMObjBase
import io.zeitwert.dddrive.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.dddrive.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.db.Tables
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.persist.sql.SqlIdProvider
import io.zeitwert.persist.sql.SqlRecordMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.util.*

@Component("objAccountPersistenceProvider")
open class ObjAccountSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
) : FMObjSqlPersistenceProviderBase<ObjAccount>(ObjAccount::class.java),
	SqlRecordMapper<ObjAccount> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjAccount) {
		val record = dslContext.fetchOne(Tables.OBJ_ACCOUNT, Tables.OBJ_ACCOUNT.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_ACCOUNT record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjAccount,
		record: ObjAccountRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.key = record.key
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.accountType = CodeAccountType.getAccountType(record.accountTypeId)
		aggregate.clientSegment = CodeClientSegment.getClientSegment(record.clientSegmentId)
		aggregate.referenceCurrency = CodeCurrency.getCurrency(record.referenceCurrencyId)
		aggregate.inflationRate = record.inflationRate
		aggregate.discountRate = record.discountRate
		aggregate.logoImageId = record.logoImgId
		aggregate.mainContactId = record.mainContactId
	}

	override fun storeRecord(aggregate: ObjAccount) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjAccount): ObjAccountRecord {
		val record = dslContext.newRecord(Tables.OBJ_ACCOUNT)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.id as Int

		record.key = aggregate.key
		record.name = aggregate.name
		record.description = aggregate.description
		record.accountTypeId = aggregate.accountType?.id
		record.clientSegmentId = aggregate.clientSegment?.id
		record.referenceCurrencyId = aggregate.referenceCurrency?.id
		record.inflationRate = aggregate.inflationRate
		record.discountRate = aggregate.discountRate
		record.logoImgId = aggregate.logoImageId as? Int
		record.mainContactId = aggregate.mainContactId as? Int

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_ACCOUNT_V, Tables.OBJ_ACCOUNT_V.ID, query)

	fun getByKey(key: String): Optional<Any> {
		val accountId = dslContext
			.select(Tables.OBJ_ACCOUNT.OBJ_ID)
			.from(Tables.OBJ_ACCOUNT)
			.where(Tables.OBJ_ACCOUNT.KEY.eq(key))
			.fetchOne(Tables.OBJ_ACCOUNT.OBJ_ID)
		return Optional.ofNullable(accountId)
	}

}
