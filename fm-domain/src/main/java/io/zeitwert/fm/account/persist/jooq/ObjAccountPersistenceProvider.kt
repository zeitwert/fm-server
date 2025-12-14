package io.zeitwert.fm.account.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.account.model.db.Tables
import io.zeitwert.fm.account.model.enums.CodeAccountType
import io.zeitwert.fm.account.model.enums.CodeClientSegment
import io.zeitwert.fm.account.model.enums.CodeCurrency
import io.zeitwert.fm.obj.model.db.Sequences
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component("objAccountPersistenceProvider")
open class ObjAccountPersistenceProvider : JooqObjPersistenceProviderBase<ObjAccount>() {

	private lateinit var _dslContext: DSLContext
	private lateinit var _repository: ObjAccountRepository

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	@Autowired
	@Lazy
	fun setRepository(repository: ObjAccountRepository) {
		this._repository = repository
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun nextAggregateId(): Any =
		dslContext()
			.nextval(Sequences.OBJ_ID_SEQ)
			.toInt()

	override fun fromAggregate(aggregate: ObjAccount): UpdatableRecord<*> = createObjRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: ObjAccount) {
		val objId = aggregate.id as Int

		val existingRecord = dslContext().fetchOne(
			Tables.OBJ_ACCOUNT,
			Tables.OBJ_ACCOUNT.OBJ_ID.eq(objId),
		)

		val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_ACCOUNT)

		record.objId = objId
		record.tenantId = aggregate.tenantId as? Int
		record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		record.name = (aggregate.getProperty("name") as? BaseProperty<String?>)?.value
		record.description = (aggregate.getProperty("description") as? BaseProperty<String?>)?.value
		record.accountTypeId = (aggregate.getProperty("accountType") as? EnumProperty<CodeAccountType>)?.value?.id
		record.clientSegmentId = (aggregate.getProperty("clientSegment") as? EnumProperty<CodeClientSegment>)?.value?.id
		record.referenceCurrencyId = (aggregate.getProperty("referenceCurrency") as? EnumProperty<CodeCurrency>)?.value?.id
		record.inflationRate = (aggregate.getProperty("inflationRate") as? BaseProperty<BigDecimal?>)?.value
		record.discountRate = (aggregate.getProperty("discountRate") as? BaseProperty<BigDecimal?>)?.value
		record.logoImgId = (aggregate.getProperty("logoImage") as? ReferenceProperty<*>)?.id as? Int
		record.mainContactId = (aggregate.getProperty("mainContact") as? ReferenceProperty<*>)?.id as? Int

		if (existingRecord != null) {
			record.update()
		} else {
			record.insert()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: ObjAccount,
		objId: Int?,
	) {
		if (objId == null) return

		val record = dslContext().fetchOne(
			Tables.OBJ_ACCOUNT,
			Tables.OBJ_ACCOUNT.OBJ_ID.eq(objId),
		) ?: return

		(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.accountId
		(aggregate.getProperty("name") as? BaseProperty<String?>)?.value = record.name
		(aggregate.getProperty("description") as? BaseProperty<String?>)?.value = record.description

		record.accountTypeId?.let { accountTypeId ->
			(aggregate.getProperty("accountType") as? EnumProperty<CodeAccountType>)?.value =
				CodeAccountType.getAccountType(accountTypeId)
		}

		record.clientSegmentId?.let { clientSegmentId ->
			(aggregate.getProperty("clientSegment") as? EnumProperty<CodeClientSegment>)?.value =
				CodeClientSegment.getClientSegment(clientSegmentId)
		}

		record.referenceCurrencyId?.let { currencyId ->
			(aggregate.getProperty("referenceCurrency") as? EnumProperty<CodeCurrency>)?.value =
				CodeCurrency.getCurrency(currencyId)
		}

		(aggregate.getProperty("inflationRate") as? BaseProperty<BigDecimal?>)?.value = record.inflationRate
		(aggregate.getProperty("discountRate") as? BaseProperty<BigDecimal?>)?.value = record.discountRate

		(aggregate.getProperty("logoImage") as? ReferenceProperty<*>)?.let { prop ->
			@Suppress("UNCHECKED_CAST")
			(prop as ReferenceProperty<Any>).id = record.logoImgId
		}

		(aggregate.getProperty("mainContact") as? ReferenceProperty<*>)?.let { prop ->
			@Suppress("UNCHECKED_CAST")
			(prop as ReferenceProperty<Any>).id = record.mainContactId
		}
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_account"
	}
}
