package io.zeitwert.fm.contact.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.PartListProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.db.Tables
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.oe.model.enums.CodeCountry
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.time.LocalDate

/** jOOQ-based persistence provider for ObjContact aggregates. */
@Component("objContactPersistenceProvider")
open class ObjContactPersistenceProvider : JooqObjPersistenceProviderBase<ObjContact>() {
	private lateinit var _dslContext: DSLContext
	private lateinit var _repository: ObjContactRepository

	@Autowired
	fun setDslContext(dslContext: DSLContext) {
		this._dslContext = dslContext
	}

	@Autowired
	@Lazy
	fun setRepository(repository: ObjContactRepository) {
		this._repository = repository
	}

	override fun dslContext(): DSLContext = _dslContext

	override fun getRepository(): ObjContactRepository = _repository

	override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

	override fun nextAggregateId(): Any = dslContext().nextval(Sequences.OBJ_ID_SEQ).toInt()

	override fun fromAggregate(aggregate: ObjContact): UpdatableRecord<*> = createObjRecord(aggregate)

	@Suppress("UNCHECKED_CAST")
	override fun storeExtension(aggregate: ObjContact) {
		val objId = aggregate.id as Int

		val existingRecord =
			dslContext().fetchOne(Tables.OBJ_CONTACT, Tables.OBJ_CONTACT.OBJ_ID.eq(objId))

		val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_CONTACT)

		record.objId = objId
		record.accountId = (aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value
		record.contactRoleId =
			(aggregate.getProperty("contactRole") as? EnumProperty<CodeContactRole>)?.value?.id
		record.salutationId =
			(aggregate.getProperty("salutation") as? EnumProperty<CodeSalutation>)?.value?.id
		record.titleId = (aggregate.getProperty("title") as? EnumProperty<CodeTitle>)?.value?.id
		record.firstName = (aggregate.getProperty("firstName") as? BaseProperty<String?>)?.value
		record.lastName = (aggregate.getProperty("lastName") as? BaseProperty<String?>)?.value
		record.birthDate = (aggregate.getProperty("birthDate") as? BaseProperty<LocalDate?>)?.value
		record.phone = (aggregate.getProperty("phone") as? BaseProperty<String?>)?.value
		record.mobile = (aggregate.getProperty("mobile") as? BaseProperty<String?>)?.value
		record.email = (aggregate.getProperty("email") as? BaseProperty<String?>)?.value
		record.description = (aggregate.getProperty("description") as? BaseProperty<String?>)?.value

		if (existingRecord != null) {
			record.update()
		} else {
			record.insert()
		}

		// Store address parts
		storeAddressParts(aggregate, objId)
	}

	@Suppress("UNCHECKED_CAST")
	private fun storeAddressParts(
		aggregate: ObjContact,
		objId: Int,
	) {
		val addressList =
			(aggregate.getProperty("addressList") as? PartListProperty<ObjContactPartAddress>)
				?.parts
				?: return

		// Delete existing addresses
		dslContext()
			.deleteFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
			.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(objId))
			.execute()

		// Insert new addresses
		addressList.forEachIndexed { index, address ->
			val record = dslContext().newRecord(Tables.OBJ_CONTACT_PART_ADDRESS)
			record.id = address.id
			record.objId = objId
			record.seqNr = index + 1
			record.addressChannelId =
				(address.getProperty("addressChannel") as? EnumProperty<CodeAddressChannel>)
					?.value
					?.id
			record.name = (address.getProperty("name") as? BaseProperty<String?>)?.value
			record.street = (address.getProperty("street") as? BaseProperty<String?>)?.value
			record.zip = (address.getProperty("zip") as? BaseProperty<String?>)?.value
			record.city = (address.getProperty("city") as? BaseProperty<String?>)?.value
			record.countryId = (address.getProperty("country") as? EnumProperty<CodeCountry>)?.value?.id
			record.insert()
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun loadExtension(
		aggregate: ObjContact,
		objId: Int?,
	) {
		if (objId == null) return

		val record =
			dslContext().fetchOne(Tables.OBJ_CONTACT, Tables.OBJ_CONTACT.OBJ_ID.eq(objId)) ?: return

		(aggregate.getProperty("accountId") as? BaseProperty<Int?>)?.value = record.accountId

		record.contactRoleId?.let { id ->
			(aggregate.getProperty("contactRole") as? EnumProperty<CodeContactRole>)?.value =
				CodeContactRole.getContactRole(id)
		}

		record.salutationId?.let { id ->
			(aggregate.getProperty("salutation") as? EnumProperty<CodeSalutation>)?.value =
				CodeSalutation.getSalutation(id)
		}

		record.titleId?.let { id ->
			(aggregate.getProperty("title") as? EnumProperty<CodeTitle>)?.value = CodeTitle.getTitle(id)
		}

		(aggregate.getProperty("firstName") as? BaseProperty<String?>)?.value = record.firstName
		(aggregate.getProperty("lastName") as? BaseProperty<String?>)?.value = record.lastName
		(aggregate.getProperty("birthDate") as? BaseProperty<LocalDate?>)?.value = record.birthDate
		(aggregate.getProperty("phone") as? BaseProperty<String?>)?.value = record.phone
		(aggregate.getProperty("mobile") as? BaseProperty<String?>)?.value = record.mobile
		(aggregate.getProperty("email") as? BaseProperty<String?>)?.value = record.email
		(aggregate.getProperty("description") as? BaseProperty<String?>)?.value = record.description

		// Load address parts
		loadAddressParts(aggregate, objId)
	}

	@Suppress("UNCHECKED_CAST")
	private fun loadAddressParts(
		aggregate: ObjContact,
		objId: Int,
	) {
		val addressList =
			aggregate.getProperty("addressList") as? PartListProperty<ObjContactPartAddress>
				?: return

		val records =
			dslContext()
				.selectFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
				.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(objId))
				.orderBy(Tables.OBJ_CONTACT_PART_ADDRESS.SEQ_NR)
				.fetch()

		for (record in records) {
			val address = addressList.addPart(record.id)

			record.addressChannelId?.let { id ->
				(address.getProperty("addressChannel") as? EnumProperty<CodeAddressChannel>)?.value =
					CodeAddressChannel.getAddressChannel(id)
			}

			(address.getProperty("name") as? BaseProperty<String?>)?.value = record.name
			(address.getProperty("street") as? BaseProperty<String?>)?.value = record.street
			(address.getProperty("zip") as? BaseProperty<String?>)?.value = record.zip
			(address.getProperty("city") as? BaseProperty<String?>)?.value = record.city

			record.countryId?.let { id ->
				(address.getProperty("country") as? EnumProperty<CodeCountry>)?.value =
					CodeCountry.getCountry(id)
			}
		}
	}

	override fun getRecordIdsByForeignKey(
		fkName: String,
		targetId: Int,
	): List<Int> {
		// Handle Contact-specific foreign keys
		if (fkName == "accountId") {
			return dslContext()
				.select(Tables.OBJ_CONTACT.OBJ_ID)
				.from(Tables.OBJ_CONTACT)
				.where(Tables.OBJ_CONTACT.ACCOUNT_ID.eq(targetId))
				.fetch(Tables.OBJ_CONTACT.OBJ_ID)
		}
		return super.getRecordIdsByForeignKey(fkName, targetId)
	}

	companion object {
		private const val AGGREGATE_TYPE_ID = "obj_contact"
	}
}
