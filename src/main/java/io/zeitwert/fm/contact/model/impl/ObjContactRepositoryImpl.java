
package io.zeitwert.fm.contact.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.time.LocalDate;
import java.util.List;

import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.base.ObjContactBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.obj.model.base.FMObjExtnRepositoryBase;

@Component("objContactRepository")
public class ObjContactRepositoryImpl extends FMObjExtnRepositoryBase<ObjContact, ObjContactVRecord>
		implements ObjContactRepository {

	private static final String AGGREGATE_TYPE = "obj_contact";

	protected ObjContactRepositoryImpl() {
		super(ObjContactRepository.class, ObjContact.class, ObjContactBase.class, AGGREGATE_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("contactRole", AggregateState.EXTN, "contact_role_id", String.class);
		this.mapField("salutation", AggregateState.EXTN, "salutation_id", String.class);
		this.mapField("title", AggregateState.EXTN, "title_id", String.class);
		this.mapField("firstName", AggregateState.EXTN, "first_name", String.class);
		this.mapField("lastName", AggregateState.EXTN, "last_name", String.class);
		this.mapField("birthDate", AggregateState.EXTN, "birth_date", LocalDate.class);
		this.mapField("phone", AggregateState.EXTN, "phone", String.class);
		this.mapField("mobile", AggregateState.EXTN, "mobile", String.class);
		this.mapField("email", AggregateState.EXTN, "email", String.class);
		this.mapField("description", AggregateState.EXTN, "description", String.class);
		this.mapCollection("addressList", "contact.addressList", ObjContactPartAddress.class);
	}

	@Override
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getAddressRepository());
	}

	@Override
	public ObjContact doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_CONTACT));
	}

	@Override
	public ObjContact doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjContactRecord contactRecord = this.dslContext().fetchOne(Tables.OBJ_CONTACT,
				Tables.OBJ_CONTACT.OBJ_ID.eq(objId));
		if (contactRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, contactRecord);
	}

	@Override
	public List<ObjContactVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_CONTACT_V, Tables.OBJ_CONTACT_V.ID, querySpec);
	}

}
