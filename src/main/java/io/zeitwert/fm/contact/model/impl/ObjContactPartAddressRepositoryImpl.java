package io.zeitwert.fm.contact.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.base.ObjContactPartAddressBase;

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
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ObjContactPartAddress> doLoad(ObjContact obj) {
		throw new UnsupportedOperationException();
	}

}
