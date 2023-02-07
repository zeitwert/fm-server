
package io.zeitwert.fm.contact.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactPartAddressRecord;
import io.zeitwert.jooq.persistence.ObjPartPersistenceProviderBase;
import io.zeitwert.jooq.persistence.PartState;

@Configuration
public class ObjContactPartAddressPersistenceProvider
		extends ObjPartPersistenceProviderBase<ObjContact, ObjContactPartAddress> {

	public ObjContactPartAddressPersistenceProvider(DSLContext dslContext) {
		super(ObjContactPartAddress.class, dslContext);
		this.mapField("addressChannel", PartState.BASE, "address_channel_id", String.class);
		this.mapField("name", PartState.BASE, "name", String.class);
		this.mapField("street", PartState.BASE, "street", String.class);
		this.mapField("zip", PartState.BASE, "zip", String.class);
		this.mapField("city", PartState.BASE, "city", String.class);
		this.mapField("country", PartState.BASE, "country_id", String.class);
	}

	@Override
	public ObjContactPartAddress doCreate(ObjContact obj) {
		ObjContactPartAddressRecord dbRecord = this.dslContext().newRecord(Tables.OBJ_CONTACT_PART_ADDRESS);
		return this.getRepositorySPI().newPart(obj, new PartState(dbRecord));
	}

	@Override
	public List<ObjContactPartAddress> doLoad(ObjContact obj) {
		Result<ObjContactPartAddressRecord> dbRecords = this.dslContext()
				.selectFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
				.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(obj.getId()))
				.orderBy(Tables.OBJ_CONTACT_PART_ADDRESS.SEQ_NR)
				.fetchInto(Tables.OBJ_CONTACT_PART_ADDRESS);
		return dbRecords.map(dbRecord -> this.getRepositorySPI().newPart(obj, new PartState(dbRecord)));
	}

}
