package io.zeitwert.fm.contact.persist.jooq

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.obj.model.Obj
import io.dddrive.path.setValueByPath
import io.zeitwert.dddrive.persist.SqlAggregatePersistenceProviderBase
import io.zeitwert.dddrive.persist.SqlAggregateRecordMapper
import io.zeitwert.dddrive.persist.SqlIdProvider
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.db.Tables
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.obj.model.base.FMObjBase
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord
import io.zeitwert.fm.obj.persist.ObjRecordMapperImpl
import org.jooq.DSLContext
import org.springframework.stereotype.Component

/** Persistence provider for ObjContact aggregates. */
@Component("objContactPersistenceProvider")
open class ObjContactPersistenceProviderImpl(
	override val dslContext: DSLContext,
) : SqlAggregatePersistenceProviderBase<ObjContact, ObjRecord, ObjContactRecord>(ObjContact::class.java),
	SqlAggregateRecordMapper<ObjContact, ObjContactRecord> {

	override val idProvider: SqlIdProvider<Obj> get() = baseRecordMapper

	override val baseRecordMapper = ObjRecordMapperImpl(dslContext)

	override val extnRecordMapper get() = this

	override fun loadRecord(aggregateId: Any): ObjContactRecord {
		val record = dslContext.fetchOne(Tables.OBJ_CONTACT, Tables.OBJ_CONTACT.OBJ_ID.eq(aggregateId as Int))
		return record ?: throw IllegalArgumentException("no OBJ_CONTACT record found for $aggregateId")
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapFromRecord(
		aggregate: ObjContact,
		record: ObjContactRecord,
	) {
		aggregate.setValueByPath("accountId", record.accountId)
		aggregate.setValueByPath("contactRole", CodeContactRole.getContactRole(record.contactRoleId))
		aggregate.setValueByPath("salutation", CodeSalutation.getSalutation(record.salutationId))
		aggregate.setValueByPath("title", CodeTitle.getTitle(record.titleId))
		aggregate.setValueByPath("firstName", record.firstName)
		aggregate.setValueByPath("lastName", record.lastName)
		aggregate.setValueByPath("birthDate", record.birthDate)
		aggregate.setValueByPath("phone", record.phone)
		aggregate.setValueByPath("mobile", record.mobile)
		aggregate.setValueByPath("email", record.email)
		aggregate.setValueByPath("description", record.description)

		// TODO: Load address parts - to be implemented later
	}

	@Suppress("UNCHECKED_CAST")
	override fun mapToRecord(aggregate: ObjContact): ObjContactRecord {
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

	override fun storeRecord(
		record: ObjContactRecord,
		aggregate: Aggregate,
	) {
		if ((aggregate as FMObjBase).isNew) {
			record.insert()
		} else {
			record.update()
		}

		// TODO: Store address parts - to be implemented later
	}

	override fun getAll(tenantId: Any): List<Any> =
		dslContext
			.select(Tables.OBJ_CONTACT.OBJ_ID)
			.from(Tables.OBJ_CONTACT)
			.where(Tables.OBJ_CONTACT.TENANT_ID.eq(tenantId as Int))
			.fetch(Tables.OBJ_CONTACT.OBJ_ID)

	override fun getByForeignKey(
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

