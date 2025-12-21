package io.zeitwert.fm.portfolio.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.dddrive.persist.base.SqlAggregatePersistenceProviderBase
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.db.Tables
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPortfolioPersistenceProvider")
open class ObjPortfolioPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjPortfolio, ObjRecord, ObjPortfolioRecord>(ObjPortfolio::class.java),
	SqlRecordMapper<ObjPortfolio, ObjPortfolioRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjPortfolioRecord {
		val record = dslContext.fetchOne(Tables.OBJ_PORTFOLIO, Tables.OBJ_PORTFOLIO.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_PORTFOLIO record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjPortfolio,
		record: ObjPortfolioRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.setValueByPath("name", record.name)
		aggregate.setValueByPath("description", record.description)
		aggregate.setValueByPath("portfolioNr", record.portfolioNr)

		// TODO: Load reference sets (includeSet, excludeSet) - to be implemented later
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjPortfolio): ObjPortfolioRecord {
		val record = dslContext.newRecord(Tables.OBJ_PORTFOLIO)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.name = aggregate.name
		record.description = aggregate.description
		record.portfolioNr = aggregate.portfolioNr

		return record
	}

	override fun storeRecord(
		record: ObjPortfolioRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}

		// TODO: Store reference sets (includeSet, excludeSet) - to be implemented later
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_PORTFOLIO.OBJ_ID)
			.from(Tables.OBJ_PORTFOLIO)
			.where(Tables.OBJ_PORTFOLIO.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_PORTFOLIO.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ_PORTFOLIO.TENANT_ID
			"accountId" -> Tables.OBJ_PORTFOLIO.ACCOUNT_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_PORTFOLIO.OBJ_ID)
			.from(Tables.OBJ_PORTFOLIO)
			.where(field.eq(targetId as Int))
			.fetch(Tables.OBJ_PORTFOLIO.OBJ_ID)
	}

}
