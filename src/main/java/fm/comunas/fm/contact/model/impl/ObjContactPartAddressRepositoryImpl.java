package fm.comunas.fm.contact.model.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.fm.contact.model.ObjContactPartAddress;
import fm.comunas.fm.contact.model.ObjContactPartAddressRepository;
import fm.comunas.fm.contact.model.base.ObjContactPartAddressBase;
import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.fm.contact.model.db.tables.records.ObjContactPartAddressRecord;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.base.ObjPartRepositoryBase;

import java.util.List;

@Component("contactPartAddressRepository")
public class ObjContactPartAddressRepositoryImpl extends ObjPartRepositoryBase<ObjContact, ObjContactPartAddress>
		implements ObjContactPartAddressRepository {

	private static final String PART_TYPE = "obj_contact_part_address";

	@Autowired
	//@formatter:off
	protected ObjContactPartAddressRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(
			ObjContact.class,
			ObjContactPartAddress.class,
			ObjContactPartAddressBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public List<ObjContactPartAddress> doLoad(ObjContact obj) {
		//@formatter:off
		Result<ObjContactPartAddressRecord> dbRecords = this.dslContext
			.selectFrom(Tables.OBJ_CONTACT_PART_ADDRESS)
			.where(Tables.OBJ_CONTACT_PART_ADDRESS.OBJ_ID.eq(obj.getId()))
			.orderBy(Tables.OBJ_CONTACT_PART_ADDRESS.SEQ_NR)
			.fetchInto(Tables.OBJ_CONTACT_PART_ADDRESS);
		//@formatter:on
		return dbRecords.map(dbRecord -> this.newPart(obj, dbRecord));
	}

	@Override
	public ObjContactPartAddress doCreate(ObjContact obj) {
		ObjContactPartAddressRecord dbRecord = this.dslContext.newRecord(Tables.OBJ_CONTACT_PART_ADDRESS);
		return this.newPart(obj, dbRecord);
	}

}
