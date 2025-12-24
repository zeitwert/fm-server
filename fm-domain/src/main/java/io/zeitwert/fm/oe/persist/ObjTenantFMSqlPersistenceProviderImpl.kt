package io.zeitwert.fm.oe.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.oe.model.ObjTenant
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.oe.model.ObjTenantFM
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantRecord
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import io.zeitwert.fm.app.model.RequestContextFM
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component("objTenantFMPersistenceProvider")
open class ObjTenantFMSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val requestCtx: RequestContextFM,
) : FMObjSqlPersistenceProviderBase<ObjTenant>(ObjTenant::class.java),
	SqlRecordMapper<ObjTenant> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjTenant) {
		val record = dslContext.fetchOne(Tables.OBJ_TENANT, Tables.OBJ_TENANT.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_TENANT record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjTenant,
		record: ObjTenantRecord,
	) {
		aggregate as ObjTenantFM
		aggregate.tenantType = CodeTenantType.getTenantType(record.tenantTypeId)
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.inflationRate = record.inflationRate
		aggregate.discountRate = record.discountRate
		aggregate.setValueByPath("logoImageId", record.logoImgId)
	}

	override fun storeRecord(aggregate: ObjTenant) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjTenant): ObjTenantRecord {
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

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, query)

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
