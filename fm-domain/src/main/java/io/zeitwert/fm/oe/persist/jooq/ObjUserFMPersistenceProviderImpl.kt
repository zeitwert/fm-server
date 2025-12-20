package io.zeitwert.fm.oe.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.core.oe.model.ObjUser
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserRecord
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserFMPersistenceProvider")
open class ObjUserFMPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjUser, ObjRecord, ObjUserRecord>(ObjUser::class.java),
	SqlAggregateRecordMapper<ObjUser, ObjUserRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjUserRecord {
		val record = dslContext.fetchOne(Tables.OBJ_USER, Tables.OBJ_USER.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_USER record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjUser,
		record: ObjUserRecord,
	) {
		aggregate.setValueByPath("email", record.email)
		aggregate.setValueByPath("name", record.name)
		aggregate.setValueByPath("description", record.description)
		aggregate.setValueByPath("role", CodeUserRole.getUserRole(record.roleList))
		aggregate.setValueByPath("password", record.password)
		aggregate.setValueByPath("needPasswordChange", record.needPasswordChange)
		aggregate.setValueByPath("avatarImageId", record.avatarImgId)

		// TODO: Load tenant set - to be implemented later
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjUser): ObjUserRecord {
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

	override fun storeRecord(
		record: ObjUserRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}

		// TODO: Store tenant set - to be implemented later
	}

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
