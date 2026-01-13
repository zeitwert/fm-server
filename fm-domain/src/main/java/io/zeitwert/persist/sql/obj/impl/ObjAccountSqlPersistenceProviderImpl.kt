package io.zeitwert.persist.sql.obj.impl

import dddrive.property.path.setValueByPath
import dddrive.query.QuerySpec
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.db.Tables
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountRecord
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.persist.ObjAccountPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.obj.base.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.*

@Component("objAccountPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class ObjAccountSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjSqlPersistenceProviderBase<ObjAccount>(ObjAccount::class.java),
	SqlRecordMapper<ObjAccount>,
	ObjAccountPersistenceProvider {

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
		aggregate.accountType = CodeAccountType.Enumeration.getAccountType(record.accountTypeId)
		aggregate.clientSegment = CodeClientSegment.Enumeration.getClientSegment(record.clientSegmentId)
		aggregate.referenceCurrency = CodeCurrency.Enumeration.getCurrency(record.referenceCurrencyId)
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

	override fun getByKey(key: String): Optional<Any> {
		val accountId = dslContext
			.select(Tables.OBJ_ACCOUNT.OBJ_ID)
			.from(Tables.OBJ_ACCOUNT)
			.where(Tables.OBJ_ACCOUNT.KEY.eq(key))
			.fetchOne(Tables.OBJ_ACCOUNT.OBJ_ID)
		return Optional.ofNullable(accountId)
	}

}
