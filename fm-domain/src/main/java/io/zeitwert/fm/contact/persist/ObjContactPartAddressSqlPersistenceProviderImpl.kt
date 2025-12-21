package io.zeitwert.fm.contact.persist

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.ddd.model.Part
import io.dddrive.core.property.model.PartListProperty
import io.zeitwert.dddrive.persist.PartSqlPersistenceProvider
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.db.Tables
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactPartAddressRecord
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel
import io.zeitwert.fm.oe.model.enums.CodeCountry
import org.jooq.DSLContext

class ObjContactPartAddressSqlPersistenceProviderImpl(
	override val dslContext: DSLContext,
	override val aggregate: Aggregate,
) : PartSqlPersistenceProvider<ObjContactPartAddress> {

	private val partsLoaded = mutableListOf<ObjContactPartAddressRecord>()
	private val partsToInsert = mutableListOf<ObjContactPartAddressRecord>()
	private val partsToUpdate = mutableListOf<ObjContactPartAddressRecord>()

	override fun beginLoad() {
		partsLoaded.clear()
		dslContext
			.fetch(
				Tables.OBJ_CONTACT_PART_ADDRESS,
				Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(aggregate.id as Int),
			).forEach {
				partsLoaded.add(it)
			}
	}

	override fun loadPartList(
		partList: PartListProperty<ObjContactPartAddress>,
		partListTypeId: String,
	) {
		if ((partList.entity as? Part<*>) != null) {
			val parentPartId = (partList.entity as Part<*>).id
			partsLoaded.filter { (it.parentPartId == parentPartId) && (it.partListTypeId == partListTypeId) }
		} else {
			partsLoaded.filter { (it.parentPartId == 0) && (it.partListTypeId == partListTypeId) }
		}.forEach {
			val part = partList.addPart(it.id)
			mapFromRecord(
				part = part,
				record = it,
			)
		}
	}

	override fun endLoad() {
		partsLoaded.clear()
	}

	fun mapFromRecord(
		part: ObjContactPartAddress,
		record: ObjContactPartAddressRecord,
	) {
		part.addressChannel = CodeAddressChannel.getAddressChannel(record.addressChannelId)
		part.name = record.name
		part.street = record.street
		part.zip = record.zip
		part.city = record.city
		part.country = CodeCountry.getCountry(record.countryId)
	}

	override fun beginStore() {
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	override fun storePartList(
		partList: PartListProperty<ObjContactPartAddress>,
		partListTypeId: String,
	) {
		val parentPartId = (partList.entity as? Part<*>)?.id
		partList.parts.forEachIndexed { idx, it ->
			val record = mapToRecord(
				part = it,
				parentPartId = parentPartId,
				partListTypeId = partListTypeId,
				seqNr = idx + 1,
			)
			if (it.meta.isNew) {
				partsToInsert.add(record)
			} else {
				partsToUpdate.add(record)
			}
		}
	}

	override fun endStore() {
		// Update existing parts
		partsToUpdate.forEach { record ->
			record.update()
		}
		// Delete removed i.e. non-updated parts
		dslContext
			.deleteFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
			.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(aggregate.id as Int))
			.and(Tables.OBJ_CONTACT_PART_ADDRESS.AVER.lt(aggregate.meta.version))
			.execute()
		// Insert new parts
		partsToInsert.forEach { record ->
			record.insert()
		}
		partsToInsert.clear()
		partsToUpdate.clear()
	}

	fun mapToRecord(
		part: ObjContactPartAddress,
		parentPartId: Int?,
		partListTypeId: String,
		seqNr: Int,
	): ObjContactPartAddressRecord {
		val record = dslContext.newRecord(Tables.OBJ_CONTACT_PART_ADDRESS)

		record.id = part.id
		record.objId = part.meta.aggregate.id as Int
		record.parentPartId = parentPartId ?: 0
		record.partListTypeId = partListTypeId
		record.seqNr = seqNr

		record.addressChannelId = part.addressChannel?.id
		record.name = part.name
		record.street = part.street
		record.zip = part.zip
		record.city = part.city
		record.countryId = part.country?.id

		record.aver = aggregate.meta.version

		return record
	}

}
