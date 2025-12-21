package io.zeitwert.fm.oe.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.oe.model.ObjTenant
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.dddrive.persist.base.SqlAggregatePersistenceProviderBase
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantRecord
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objTenantFMPersistenceProvider")
open class ObjTenantFMPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjTenant, ObjRecord, ObjTenantRecord>(ObjTenant::class.java),
	SqlRecordMapper<ObjTenant, ObjTenantRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjTenantRecord {
		val record = dslContext.fetchOne(Tables.OBJ_TENANT, Tables.OBJ_TENANT.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_TENANT record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjTenant,
		record: ObjTenantRecord,
	) {
		aggregate.setValueByPath("tenantType", CodeTenantType.getTenantType(record.tenantTypeId))
		aggregate.setValueByPath("name", record.name)
		aggregate.setValueByPath("description", record.description)
		aggregate.setValueByPath("inflationRate", record.inflationRate)
		aggregate.setValueByPath("discountRate", record.discountRate)
		aggregate.setValueByPath("logoImageId", record.logoImgId)
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjTenant): ObjTenantRecord {
		val record = dslContext.newRecord(Tables.OBJ_TENANT)
		aggregate as ObjTenantFM

		record.objId = aggregate.id as Int
		record.tenantTypeId = aggregate.tenantType?.id
		record.name = aggregate.name
		record.description = aggregate.description
		record.inflationRate = aggregate.inflationRate
		record.discountRate = aggregate.discountRate
		record.logoImgId = aggregate.logoImageId as? Int

		return record
	}

	override fun storeRecord(
		record: ObjTenantRecord,
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
			.select(Tables.OBJ_TENANT.OBJ_ID)
			.from(Tables.OBJ_TENANT)
			.fetch(Tables.OBJ_TENANT.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		// ObjTenant typically doesn't have foreign keys to other entities
		return null
	}

}
