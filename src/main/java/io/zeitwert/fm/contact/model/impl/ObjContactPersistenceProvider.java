
package io.zeitwert.fm.contact.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.base.ObjContactBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord;
import io.zeitwert.fm.obj.model.base.FMObjPersistenceProviderBase;

@Configuration("contactPersistenceProvider")
public class ObjContactPersistenceProvider extends FMObjPersistenceProviderBase<ObjContact> {

	public ObjContactPersistenceProvider(DSLContext dslContext) {
		super(ObjContactRepository.class, ObjContactBase.class, dslContext);
		this.mapField("extnAccountId", DbTableType.EXTN, "account_id", Integer.class);
		this.mapField("contactRole", DbTableType.EXTN, "contact_role_id", String.class);
		this.mapField("salutation", DbTableType.EXTN, "salutation_id", String.class);
		this.mapField("title", DbTableType.EXTN, "title_id", String.class);
		this.mapField("firstName", DbTableType.EXTN, "first_name", String.class);
		this.mapField("lastName", DbTableType.EXTN, "last_name", String.class);
		this.mapField("birthDate", DbTableType.EXTN, "birth_date", LocalDate.class);
		this.mapField("phone", DbTableType.EXTN, "phone", String.class);
		this.mapField("mobile", DbTableType.EXTN, "mobile", String.class);
		this.mapField("email", DbTableType.EXTN, "email", String.class);
		this.mapField("description", DbTableType.EXTN, "description", String.class);
		this.mapCollection("addressList", "contact.addressList", ObjContactPartAddress.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjContact.class;
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public ObjContact doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_CONTACT));
	}

	@Override
	public ObjContact doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjContactRecord contactRecord = this.getDSLContext().fetchOne(Tables.OBJ_CONTACT,
				Tables.OBJ_CONTACT.OBJ_ID.eq(objId));
		if (contactRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, contactRecord);
	}

}
