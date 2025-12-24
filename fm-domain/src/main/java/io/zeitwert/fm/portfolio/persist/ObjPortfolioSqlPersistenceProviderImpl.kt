package io.zeitwert.fm.portfolio.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.obj.model.Obj
import io.dddrive.property.model.ReferenceSetProperty
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjPartItemSqlPersistenceProviderImpl
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.db.Tables
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objPortfolioPersistenceProvider")
open class ObjPortfolioSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : FMObjSqlPersistenceProviderBase<ObjPortfolio>(ObjPortfolio::class.java),
	SqlRecordMapper<ObjPortfolio> {

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

	@Suppress("UNCHECKED_CAST")
	override fun doLoadParts(aggregate: ObjPortfolio) {
		super.doLoadParts(aggregate)
		val includeSet = aggregate.getProperty("includeSet", Obj::class) as ReferenceSetProperty<Obj>
		val excludeSet = aggregate.getProperty("excludeSet", Obj::class) as ReferenceSetProperty<Obj>
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			items("portfolio.includeList").forEach {
				it.toIntOrNull()?.let { objId -> includeSet.addItem(objId) }
			}
			items("portfolio.excludeList").forEach {
				it.toIntOrNull()?.let { objId -> excludeSet.addItem(objId) }
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doStoreParts(aggregate: ObjPortfolio) {
		super.doStoreParts(aggregate)
		val includeSet = aggregate.getProperty("includeSet", Obj::class) as ReferenceSetProperty<Obj>
		val excludeSet = aggregate.getProperty("excludeSet", Obj::class) as ReferenceSetProperty<Obj>
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			addItems("portfolio.includeList", includeSet.items.map { it.toString() })
			addItems("portfolio.excludeList", excludeSet.items.map { it.toString() })
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjPortfolio): ObjPortfolioRecord {
		val record = dslContext.newRecord(Tables.OBJ_PORTFOLIO)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.name = aggregate.name
		record.description = aggregate.description
		record.portfolioNr = aggregate.portfolioNr

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_PORTFOLIO_V, Tables.OBJ_PORTFOLIO_V.ID, query)

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
