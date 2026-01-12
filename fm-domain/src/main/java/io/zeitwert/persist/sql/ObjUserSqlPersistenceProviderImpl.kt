package io.zeitwert.persist.sql

import dddrive.query.QuerySpec
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.db.tables.records.ObjUserRecord
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import io.zeitwert.persist.ObjUserPersistenceProvider
import io.zeitwert.persist.sql.ddd.SqlIdProvider
import io.zeitwert.persist.sql.ddd.SqlRecordMapper
import io.zeitwert.persist.sql.obj.ObjPartItemSqlPersistenceProviderImpl
import io.zeitwert.persist.sql.obj.ObjRecordMapperImpl
import io.zeitwert.persist.sql.obj.ObjSqlPersistenceProviderBase
import org.jooq.DSLContext
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.*

@Component("objUserPersistenceProvider")
@Primary
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "sql", matchIfMissing = true)
open class ObjUserSqlPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
	private val dslContextProvider: ObjectProvider<DSLContext>,
) : ObjSqlPersistenceProviderBase<ObjUser>(ObjUser::class.java),
	SqlRecordMapper<ObjUser>,
	ObjUserPersistenceProvider {

	override val dslContext: DSLContext
		get() = dslContextProvider.getObject()

	override val hasAccount = false

	override val idProvider: SqlIdProvider
		get() = baseRecordMapper

	override val baseRecordMapper: ObjRecordMapperImpl
		get() = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper
		get() = this

	override fun loadRecord(aggregate: ObjUser) {
		val record =
			dslContext.fetchOne(Tables.OBJ_USER, Tables.OBJ_USER.OBJ_ID.eq(aggregate.id as Int))
		record ?: throw IllegalArgumentException("no OBJ_USER record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	private fun mapFromRecord(
		aggregate: ObjUser,
		record: ObjUserRecord,
	) {
		aggregate as ObjUser
		aggregate.email = record.email
		aggregate.name = record.name
		aggregate.description = record.description
		aggregate.role = CodeUserRole.Enumeration.getUserRole(record.roleList)
		aggregate.password = record.password
		aggregate.needPasswordChange = record.needPasswordChange
		aggregate.avatarImageId = record.avatarImgId
	}

	override fun storeRecord(aggregate: ObjUser) {
		val record = mapToRecord(aggregate)
		if (aggregate.meta.isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	override fun doLoadParts(aggregate: ObjUser) {
		super.doLoadParts(aggregate)
		aggregate as ObjUser
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			items("user.tenantList").forEach {
				it.toIntOrNull()?.let { tenantId -> aggregate.tenantSet.add(tenantId) }
			}
		}
	}

	override fun doStoreParts(aggregate: ObjUser) {
		super.doStoreParts(aggregate)
		aggregate as ObjUser
		ObjPartItemSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			addItems("user.tenantList", aggregate.tenantSet.map { it.toString() })
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjUser): ObjUserRecord {
		val record = dslContext.newRecord(Tables.OBJ_USER)
		aggregate as ObjUser

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

	override fun getByEmail(email: String): Optional<Any> {
		val userId =
			dslContext
				.select(Tables.OBJ_USER.OBJ_ID)
				.from(Tables.OBJ_USER)
				.where(Tables.OBJ_USER.EMAIL.eq(email))
				.fetchOne(Tables.OBJ_USER.OBJ_ID)
		return Optional.ofNullable(userId)
	}

}
