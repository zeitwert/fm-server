
package io.zeitwert.fm.contact.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.base.ObjContactBase;
import io.zeitwert.fm.contact.model.base.ObjContactFields;
import io.zeitwert.fm.contact.model.db.Tables;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactRecord;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objContactRepository")
public class ObjContactRepositoryImpl extends FMObjRepositoryBase<ObjContact, ObjContactVRecord>
		implements ObjContactRepository {

	private static final String AGGREGATE_TYPE = "obj_contact";

	private final ObjContactPartAddressRepository addressRepository;
	private final CodePartListType addressListType;

	//@formatter:off
	protected ObjContactRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjContactPartAddressRepository addressRepository,
		final ObjNoteRepository noteRepository
	) {
		super(
			ObjContactRepository.class,
			ObjContact.class,
			ObjContactBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository,
			noteRepository
		);
		this.addressRepository = addressRepository;
		this.addressListType = this.getAppContext().getPartListType(ObjContactFields.ADDRESS_LIST);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getAddressRepository());
	}

	@Override
	public ObjContactPartAddressRepository getAddressRepository() {
		return this.addressRepository;
	}

	@Override
	public CodePartListType getAddressListType() {
		return this.addressListType;
	}

	@Override
	protected boolean hasAccountId() {
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

	@Override
	public List<ObjContactVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_CONTACT_V, Tables.OBJ_CONTACT_V.ID, querySpec);
	}

	@Override
	public void changeOwner(List<ObjContact> contacts, ObjUser user) {
		contacts.stream().forEach((contact) -> {
			contact.setOwner(user);
			this.store(contact);
		});
	}

}
