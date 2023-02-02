
package io.zeitwert.fm.contact.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartPersistenceProviderBase;
import io.zeitwert.fm.contact.model.base.ObjContactPartAddressBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactPartAddressRecord;
import io.zeitwert.ddd.persistence.jooq.PartState;

@Configuration
public class ObjContactPartAddressPersistenceProvider
		extends ObjPartPersistenceProviderBase<ObjContact, ObjContactPartAddress> {

	public ObjContactPartAddressPersistenceProvider(DSLContext dslContext) {
		super(ObjContact.class, ObjContactPartAddressRepository.class, ObjContactPartAddressBase.class, dslContext);
		this.mapField("addressChannel", BASE, "address_channel_id", String.class);
		this.mapField("name", BASE, "name", String.class);
		this.mapField("street", BASE, "street", String.class);
		this.mapField("zip", BASE, "zip", String.class);
		this.mapField("city", BASE, "city", String.class);
		this.mapField("country", BASE, "country_id", String.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjContactPartAddress.class;
	}

	@Override
	public ObjContactPartAddress doCreate(ObjContact obj) {
		ObjContactPartAddressRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_CONTACT_PART_ADDRESS);
		return this.newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjContactPartAddress> doLoad(ObjContact obj) {
		Result<ObjContactPartAddressRecord> dbRecords = this.getDSLContext()
				.selectFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
				.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_CONTACT_PART_ADDRESS.SEQ_NR)
				.fetchInto(Tables.OBJ_CONTACT_PART_ADDRESS);
		return dbRecords.map(dbRecord -> this.newPart(obj, new PartState(dbRecord)));
	}

}
