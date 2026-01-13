package io.zeitwert.persist.sql.obj.impl

import dddrive.query.QuerySpec
import io.zeitwert.app.obj.model.base.FMObjBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.db.Tables
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.persist.ObjPortfolioPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.obj.base.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component("objPortfolioPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class ObjPortfolioSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjSqlPersistenceProviderBase<ObjPortfolio>(ObjPortfolio::class.java),
	SqlRecordMapper<ObjPortfolio>,
	ObjPortfolioPersistenceProvider {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjPortfolio) {
		val record = dslContext.fetchOne(Tables.OBJ_PORTFOLIO, Tables.OBJ_PORTFOLIO.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_PORTFOLIO record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjPortfolio,
		record: ObjPortfolioRecord,
	) {
		aggregate.accountId = record.accountId
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.portfolioNr = record.portfolioNr
	}

	override fun storeRecord(aggregate: ObjPortfolio) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	override fun doLoadParts(aggregate: ObjPortfolio) {
		super.doLoadParts(aggregate)
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			items("portfolio.includeList").forEach {
				it.toIntOrNull()?.let { objId -> aggregate.includeSet.add(objId) }
			}
			items("portfolio.excludeList").forEach {
				it.toIntOrNull()?.let { objId -> aggregate.excludeSet.add(objId) }
			}
		}
	}

	override fun doStoreParts(aggregate: ObjPortfolio) {
		super.doStoreParts(aggregate)
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			addItems("portfolio.includeList", aggregate.includeSet.map { it.toString() })
			addItems("portfolio.excludeList", aggregate.excludeSet.map { it.toString() })
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjPortfolio): ObjPortfolioRecord {
		val record = dslContext.newRecord(Tables.OBJ_PORTFOLIO)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as Int

		record.name = aggregate.name
		record.description = aggregate.description
		record.portfolioNr = aggregate.portfolioNr

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_PORTFOLIO_V, Tables.OBJ_PORTFOLIO_V.ID, query)

}
