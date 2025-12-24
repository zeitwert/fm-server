package io.zeitwert.fm.oe.persist

import io.crnk.core.queryspec.QuerySpec
import io.dddrive.oe.model.ObjTenant
import io.dddrive.oe.model.ObjUser
import io.dddrive.path.setValueByPath
import io.dddrive.property.model.ReferenceSetProperty
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjPartItemSqlPersistenceProviderImpl
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserRecord
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserFMPersistenceProvider")
open class ObjUserFMSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : FMObjSqlPersistenceProviderBase<ObjUser>(ObjUser::class.java),
	SqlRecordMapper<ObjUser> {

	override val idProvider: SqlIdProvider get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregate: ObjUser) {
		val record = dslContext.fetchOne(Tables.OBJ_USER, Tables.OBJ_USER.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_USER record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjUser,
		record: ObjUserRecord,
	) {
		aggregate as ObjUserFM
		aggregate.email = record.email
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.role = CodeUserRole.getUserRole(record.roleList)
		aggregate.password = record.password
		aggregate.needPasswordChange = record.needPasswordChange
		aggregate.setValueByPath("avatarImageId", record.avatarImgId)
	}

	override fun storeRecord(aggregate: ObjUser) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doLoadParts(aggregate: ObjUser) {
		super.doLoadParts(aggregate)
		val tenantSet = aggregate.getProperty("tenantSet", ObjTenant::class) as ReferenceSetProperty<ObjTenant>
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			items("user.tenantList").forEach {
				it.toIntOrNull()?.let { tenantId ->
					tenantSet.addItem(tenantId)
				}
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doStoreParts(aggregate: ObjUser) {
		super.doStoreParts(aggregate)
		val tenantSet = aggregate.getProperty("tenantSet", ObjTenant::class) as ReferenceSetProperty<ObjTenant>
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			addItems("user.tenantList", tenantSet.items.map { it.toString() })
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjUser): ObjUserRecord {
		val record = dslContext.newRecord(Tables.OBJ_USER)
		aggregate as ObjUserFM

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.email = aggregate.email
		record.name = aggregate.name
		record.description = aggregate.description
		record.roleList = aggregate.role?.id
		record.password = aggregate.password
		record.needPasswordChange = aggregate.needPasswordChange
		record.avatarImgId = aggregate.avatarImageId as? Int

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_USER_V, Tables.OBJ_USER_V.ID, query)

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_USER.OBJ_ID)
			.from(Tables.OBJ_USER)
			.where(Tables.OBJ_USER.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_USER.OBJ_ID)

	override fun getByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ_USER.TENANT_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_USER.OBJ_ID)
			.from(Tables.OBJ_USER)
			.where(field.eq(targetId as Int))
			.fetch(Tables.OBJ_USER.OBJ_ID)
	}

	fun getByEmail(email: String): Optional<Any> {
		val userId = dslContext
			.select(Tables.OBJ_USER.OBJ_ID)
			.from(Tables.OBJ_USER)
			.where(Tables.OBJ_USER.EMAIL.eq(email))
			.fetchOne(Tables.OBJ_USER.OBJ_ID)
			?: return Optional.empty()
		return Optional.ofNullable(userId)
	}

}
