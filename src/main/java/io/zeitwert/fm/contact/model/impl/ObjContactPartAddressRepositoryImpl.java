package io.zeitwert.fm.contact.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.jooq.ddd.PartState;
import io.dddrive.jooq.obj.JooqObjPartRepositoryBase;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.base.ObjContactPartAddressBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactPartAddressRecord;

@Component("contactPartAddressRepository")
public class ObjContactPartAddressRepositoryImpl extends JooqObjPartRepositoryBase<ObjContact, ObjContactPartAddress>
		implements ObjContactPartAddressRepository {

	private static final String PART_TYPE = "obj_contact_part_address";

	protected ObjContactPartAddressRepositoryImpl(AppContext appContext, DSLContext dslContext) {
		super(ObjContact.class, ObjContactPartAddress.class, ObjContactPartAddressBase.class, PART_TYPE, appContext,
				dslContext);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
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
