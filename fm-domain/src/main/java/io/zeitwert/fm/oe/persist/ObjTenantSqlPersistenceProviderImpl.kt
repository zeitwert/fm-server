package io.zeitwert.fm.oe.persist

import dddrive.ddd.query.QuerySpec
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.db.tables.records.ObjTenantRecord
import io.zeitwert.fm.oe.model.enums.CodeTenantType
import org.jooq.DSLContext
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component
import java.util.*

@Component("objTenantPersistenceProvider")
open class ObjTenantSqlPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	private val dslContextProvider: ObjectProvider<DSLContext>,
) : FMObjSqlPersistenceProviderBase<ObjTenant>(ObjTenant::class.java),
	SqlRecordMapper<ObjTenant> {

	override val dslContext: DSLContext
		get() = dslContextProvider.getObject()

	override val hasAccount = false

	override val idProvider: SqlIdProvider
		get() = baseRecordMapper

	override val baseRecordMapper: ObjRecordMapperImpl
		get() = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper
		get() = this

	override fun loadRecord(aggregate: ObjTenant) {
		val record =
			dslContext.fetchOne(Tables.OBJ_TENANT, Tables.OBJ_TENANT.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_TENANT record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	private fun mapFromRecord(
		aggregate: ObjTenant,
		record: ObjTenantRecord,
	) {
		aggregate.key = record.key
		aggregate.tenantType = CodeTenantType.getTenantType(record.tenantTypeId)
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.inflationRate = record.inflationRate
		aggregate.discountRate = record.discountRate
		aggregate.logoImageId = record.logoImgId
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
		aggregate as ObjTenant

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.id as Int
		record.key = aggregate.key
		record.tenantTypeId = aggregate.tenantType?.id
		record.name = aggregate.name
		record.description = aggregate.description
		record.inflationRate = aggregate.inflationRate
		record.discountRate = aggregate.discountRate
		record.logoImgId = aggregate.logoImageId as? Int

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_TENANT_V, Tables.OBJ_TENANT_V.ID, query)

	fun getByKey(key: String): Optional<Any> {
		val tenantId =
			dslContext
				.select(Tables.OBJ_TENANT.OBJ_ID)
				.from(Tables.OBJ_TENANT)
				.where(Tables.OBJ_TENANT.KEY.eq(key))
				.fetchOne(Tables.OBJ_TENANT.OBJ_ID)
		return Optional.ofNullable(tenantId)
	}

}
