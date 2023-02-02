package io.zeitwert.fm.contact.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.base.ObjContactPartAddressBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactPartAddressRecord;

@Component("contactPartAddressRepository")
public class ObjContactPartAddressRepositoryImpl extends ObjPartRepositoryBase<ObjContact, ObjContactPartAddress>
		implements ObjContactPartAddressRepository {

	private static final String PART_TYPE = "obj_contact_part_address";

	protected ObjContactPartAddressRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(ObjContact.class, ObjContactPartAddress.class, ObjContactPartAddressBase.class, PART_TYPE, appContext,
				dslContext);
	}

	@Override
	public ObjContactPartAddress doCreate(ObjContact obj) {
		ObjContactPartAddressRecord dbRecord = this.getDSLContext().newRecord(Tables.OBJ_CONTACT_PART_ADDRESS);
		return this.newPart(obj, dbRecord);
	}

	@Override
	public List<ObjContactPartAddress> doLoad(ObjContact obj) {
		//@formatter:off
		Result<ObjContactPartAddressRecord> dbRecords = this.getDSLContext()
			.selectFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
			.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_CONTACT_PART_ADDRESS.SEQ_NR)
			.fetchInto(Tables.OBJ_CONTACT_PART_ADDRESS);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

}
