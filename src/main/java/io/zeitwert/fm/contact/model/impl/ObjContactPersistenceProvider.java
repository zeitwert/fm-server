
package io.zeitwert.fm.contact.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.base.ObjContactBase;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord;
import io.zeitwert.fm.obj.model.base.FMObjPersistenceProviderBase;

@Configuration("contactPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjContactPersistenceProvider extends FMObjPersistenceProviderBase<ObjContact> {

	public ObjContactPersistenceProvider(DSLContext dslContext) {
		super(ObjContactRepository.class, ObjContactBase.class, dslContext);
		this.mapField("extnAccountId", EXTN, "account_id", Integer.class);
		this.mapField("contactRole", EXTN, "contact_role_id", String.class);
		this.mapField("salutation", EXTN, "salutation_id", String.class);
		this.mapField("title", EXTN, "title_id", String.class);
		this.mapField("firstName", EXTN, "first_name", String.class);
		this.mapField("lastName", EXTN, "last_name", String.class);
		this.mapField("birthDate", EXTN, "birth_date", LocalDate.class);
		this.mapField("phone", EXTN, "phone", String.class);
		this.mapField("mobile", EXTN, "mobile", String.class);
		this.mapField("email", EXTN, "email", String.class);
		this.mapField("description", EXTN, "description", String.class);
		this.mapCollection("addressList", "contact.addressList", ObjContactPartAddress.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjContact.class;
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
