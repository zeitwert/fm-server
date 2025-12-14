package io.zeitwert.fm.portfolio.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.ReferenceSetProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.obj.model.db.tables.ObjPartItem
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import io.zeitwert.fm.portfolio.model.db.Tables
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component("objPortfolioPersistenceProvider")
open class ObjPortfolioPersistenceProvider : JooqObjPersistenceProviderBase<ObjPortfolio>() {

	private lateinit var _dslContext: DSLContext
	private lateinit var _repository: ObjPortfolioRepository

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	@Autowired
	@Lazy
	fun setRepository(repository: ObjPortfolioRepository) {
		this._repository = repository
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun nextAggregateId(): Any =
		dslContext()
			.nextval(Sequences.OBJ_ID_SEQ)
			.toInt()

	override fun fromAggregate(aggregate: ObjPortfolio): UpdatableRecord<*> = createObjRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: ObjPortfolio) {
		val objId = aggregate.id as Int

		val existingRecord = dslContext().fetchOne(
			Tables.OBJ_PORTFOLIO,
			Tables.OBJ_PORTFOLIO.OBJ_ID.eq(objId),
		)

		val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_PORTFOLIO)

		record.objId = objId
		record.tenantId = aggregate.tenantId as? Int
		record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		record.name = (aggregate.getProperty("name") as? BaseProperty<String?>)?.value
		record.description = (aggregate.getProperty("description") as? BaseProperty<String?>)?.value
		record.portfolioNr = (aggregate.getProperty("portfolioNr") as? BaseProperty<String?>)?.value

		if (existingRecord != null) {
			record.update()
		} else {
			record.insert()
		}

		storeReferenceSet(aggregate, objId, "includeSet", INCLUDE_LIST_TYPE)
		storeReferenceSet(aggregate, objId, "excludeSet", EXCLUDE_LIST_TYPE)
	}

	private fun storeReferenceSet(
		aggregate: ObjPortfolio,
		objId: Int,
		propertyName: String,
		listTypeId: String,
	) {
		dslContext()
			.deleteFrom(ObjPartItem.OBJ_PART_ITEM)
			.where(ObjPartItem.OBJ_PART_ITEM.OBJ_ID.eq(objId))
			.and(ObjPartItem.OBJ_PART_ITEM.PART_LIST_TYPE_ID.eq(listTypeId))
			.execute()

		@Suppress("UNCHECKED_CAST")
		val prop = aggregate.getProperty(propertyName) as? ReferenceSetProperty<*>
		val items = prop?.items ?: return

		items.forEachIndexed { index, itemId ->
			val record = dslContext().newRecord(ObjPartItem.OBJ_PART_ITEM)
			record.objId = objId
			record.parentPartId = 0
			record.partListTypeId = listTypeId
			record.seqNr = index
			record.itemId = itemId.toString()
			record.insert()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: ObjPortfolio,
		objId: Int?,
	) {
		if (objId == null) return

		val record = dslContext().fetchOne(
			Tables.OBJ_PORTFOLIO,
			Tables.OBJ_PORTFOLIO.OBJ_ID.eq(objId),
		) ?: return

		(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.accountId
		(aggregate.getProperty("name") as? BaseProperty<String?>)?.value = record.name
		(aggregate.getProperty("description") as? BaseProperty<String?>)?.value = record.description
		(aggregate.getProperty("portfolioNr") as? BaseProperty<String?>)?.value = record.portfolioNr

		loadReferenceSet(aggregate, objId, "includeSet", INCLUDE_LIST_TYPE)
		loadReferenceSet(aggregate, objId, "excludeSet", EXCLUDE_LIST_TYPE)
	}

	private fun loadReferenceSet(
		aggregate: ObjPortfolio,
		objId: Int,
		propertyName: String,
		listTypeId: String,
	) {
		@Suppress("UNCHECKED_CAST")
		val prop = aggregate.getProperty(propertyName) as? ReferenceSetProperty<*> ?: return

		val items = dslContext()
			.selectFrom(ObjPartItem.OBJ_PART_ITEM)
			.where(ObjPartItem.OBJ_PART_ITEM.OBJ_ID.eq(objId))
			.and(ObjPartItem.OBJ_PART_ITEM.PART_LIST_TYPE_ID.eq(listTypeId))
			.orderBy(ObjPartItem.OBJ_PART_ITEM.SEQ_NR)
			.fetch()

		for (item in items) {
			prop.addItem(item.itemId.toInt())
		}
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_portfolio"
		private const val INCLUDE_LIST_TYPE = "portfolio.includeList"
		private const val EXCLUDE_LIST_TYPE = "portfolio.excludeList"
	}

}
