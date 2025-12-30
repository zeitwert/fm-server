package io.zeitwert.fm.contact.persist

import io.crnk.core.queryspec.QuerySpec
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.dddrive.persist.SqlRecordMapper
import io.zeitwert.fm.app.model.SessionContextFM
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.db.Tables
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.persist.FMObjSqlPersistenceProviderBase
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.springframework.stereotype.Component

/** Persistence provider for ObjContact aggregates. */
@Component("objContactPersistenceProvider")
open class ObjContactSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val requestCtx: SessionContextFM,
) : FMObjSqlPersistenceProviderBase<ObjContact>(ObjContact::class.java),
	SqlRecordMapper<ObjContact> {

	override val idProvider: SqlIdProvider
		get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper
		get() = this

	override fun loadRecord(aggregate: ObjContact) {
		val record =
			dslContext.fetchOne(
				Tables.OBJ_CONTACT,
				Tables.OBJ_CONTACT.OBJ_ID.eq(aggregate.id as Int),
			)
		record ?: throw IllegalArgumentException("no OBJ_CONTACT record found for ${aggregate.id}")
		mapFromRecord(aggregate, record)
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapFromRecord(
		aggregate: ObjContact,
		record: ObjContactRecord,
	) {
		aggregate.accountId = record.accountId
		aggregate.contactRole = CodeContactRole.getContactRole(record.contactRoleId)
		aggregate.salutation = CodeSalutation.getSalutation(record.salutationId)
		aggregate.title = CodeTitle.getTitle(record.titleId)
		aggregate.firstName = record.firstName
		aggregate.lastName = record.lastName
		aggregate.birthDate = record.birthDate
		aggregate.phone = record.phone
		aggregate.mobile = record.mobile
		aggregate.email = record.email
		aggregate.description = record.description
	}

	override fun storeRecord(aggregate: ObjContact) {
		val record = mapToRecord(aggregate)
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doLoadParts(aggregate: ObjContact) {
		super.doLoadParts(aggregate)
		ObjContactPartAddressSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			// Load mail addresses and electronic addresses as separate part lists
			loadPartList(aggregate.mailAddressList, "contact.mailAddressList")
			loadPartList(aggregate.electronicAddressList, "contact.electronicAddressList")
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun doStoreParts(aggregate: ObjContact) {
		super.doStoreParts(aggregate)
		ObjContactPartAddressSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			// Store mail addresses and electronic addresses as separate part lists
			storePartList(aggregate.mailAddressList, "contact.mailAddressList")
			storePartList(aggregate.electronicAddressList, "contact.electronicAddressList")
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun mapToRecord(aggregate: ObjContact): ObjContactRecord {
		val record = dslContext.newRecord(Tables.OBJ_CONTACT)

		record.objId = aggregate.id as Int
		record.tenantId = aggregate.tenantId as Int
		record.accountId = aggregate.accountId as? Int
		record.contactRoleId = aggregate.contactRole?.id
		record.salutationId = aggregate.salutation?.id
		record.titleId = aggregate.title?.id
		record.firstName = aggregate.firstName
		record.lastName = aggregate.lastName
		record.birthDate = aggregate.birthDate
		record.phone = aggregate.phone
		record.mobile = aggregate.mobile
		record.email = aggregate.email
		record.description = aggregate.description

		return record
	}

	override fun doFind(query: QuerySpec): List<Any> = doFind(Tables.OBJ_CONTACT_V, Tables.OBJ_CONTACT_V.ID, query)

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_CONTACT.OBJ_ID)
			.from(Tables.OBJ_CONTACT)
			.where(Tables.OBJ_CONTACT.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_CONTACT.OBJ_ID)

	override fun getIdsByForeignKey(
		aggregateTypeId: String,
		fkName: String,
		targetId: Any,
	): List<Any>? {
		val field = when (fkName) {
			"tenantId" -> Tables.OBJ_CONTACT.TENANT_ID
			"accountId" -> Tables.OBJ_CONTACT.ACCOUNT_ID
			else -> return null
		}
		return dslContext
			.select(Tables.OBJ_CONTACT.OBJ_ID)
			.from(Tables.OBJ_CONTACT)
			.where(field.eq(targetId as Int))
			.fetch(Tables.OBJ_CONTACT.OBJ_ID)
	}

}
