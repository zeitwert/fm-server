package io.zeitwert.fm.oe.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.ObjTenantFMRepository
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component("objTenantFMPersistenceProvider")
open class ObjTenantFMPersistenceProvider : JooqObjPersistenceProviderBase<ObjTenantFM>() {

	private lateinit var _dslContext: DSLContext
	private lateinit var _repository: ObjTenantFMRepository

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	@Autowired
	@Lazy
	fun setRepository(repository: ObjTenantFMRepository) {
		this._repository = repository
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun nextAggregateId(): Any =
		dslContext()
			.nextval(Sequences.OBJ_ID_SEQ)
			.toInt()

	override fun fromAggregate(aggregate: ObjTenantFM): UpdatableRecord<*> = createObjRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: ObjTenantFM) {
		val objId = aggregate.id as Int

		val existingRecord = dslContext().fetchOne(
			Tables.OBJ_TENANT,
			Tables.OBJ_TENANT.OBJ_ID.eq(objId),
		)

		val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_TENANT)

		record.objId = objId
		record.tenantTypeId = (aggregate.getProperty("tenantType") as? EnumProperty<CodeTenantType>)?.value?.id
		record.name = (aggregate.getProperty("name") as? BaseProperty<String?>)?.value
		record.description = (aggregate.getProperty("description") as? BaseProperty<String?>)?.value
		record.inflationRate = (aggregate.getProperty("inflationRate") as? BaseProperty<BigDecimal?>)?.value
		record.discountRate = (aggregate.getProperty("discountRate") as? BaseProperty<BigDecimal?>)?.value
		record.logoImgId = (aggregate.getProperty("logoImage") as? ReferenceProperty<*>)?.id as? Int

		if (existingRecord != null) {
			record.update()
		} else {
			record.insert()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: ObjTenantFM,
		objId: Int?,
	) {
		if (objId == null) return

		val record = dslContext().fetchOne(
			Tables.OBJ_TENANT,
			Tables.OBJ_TENANT.OBJ_ID.eq(objId),
		) ?: return

		record.tenantTypeId?.let { tenantTypeId ->
			(aggregate.getProperty("tenantType") as? EnumProperty<CodeTenantType>)?.value =
				CodeTenantType.getTenantType(tenantTypeId)
		}

		(aggregate.getProperty("name") as? BaseProperty<String?>)?.value = record.name
		(aggregate.getProperty("description") as? BaseProperty<String?>)?.value = record.description
		(aggregate.getProperty("inflationRate") as? BaseProperty<BigDecimal?>)?.value = record.inflationRate
		(aggregate.getProperty("discountRate") as? BaseProperty<BigDecimal?>)?.value = record.discountRate

		(aggregate.getProperty("logoImage") as? ReferenceProperty<*>)?.let { prop ->
			@Suppress("UNCHECKED_CAST")
			(prop as ReferenceProperty<Any>).id = record.logoImgId
		}
	}

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_tenant"
	}

}
