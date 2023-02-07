
package io.zeitwert.fm.contact.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.time.LocalDate;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.persistence.jooq.base.ObjExtnPersistenceProviderBase;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord;

@Configuration("contactPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjContactPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjContact> {

	public ObjContactPersistenceProvider(DSLContext dslContext) {
		super(ObjContact.class, dslContext);
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

}
