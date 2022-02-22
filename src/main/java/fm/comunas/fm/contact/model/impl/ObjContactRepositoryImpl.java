
package fm.comunas.fm.contact.model.impl;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.fm.contact.model.ObjContactPartAddressRepository;
import fm.comunas.fm.contact.model.ObjContactRepository;
import fm.comunas.fm.contact.model.base.ObjContactBase;
import fm.comunas.fm.contact.model.base.ObjContactFields;
import fm.comunas.fm.contact.model.db.Tables;
import fm.comunas.fm.contact.model.db.tables.records.ObjContactRecord;
import fm.comunas.fm.contact.model.db.tables.records.ObjContactVRecord;
import fm.comunas.fm.obj.model.ObjPartNoteRepository;
import fm.comunas.fm.obj.model.base.FMObjRepositoryBase;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.ObjPartItemRepository;
import fm.comunas.ddd.obj.model.ObjPartTransitionRepository;
import fm.comunas.ddd.oe.model.ObjUser;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;

@Component("objContactRepository")
public class ObjContactRepositoryImpl extends FMObjRepositoryBase<ObjContact, ObjContactVRecord>
		implements ObjContactRepository {

	private static final String ITEM_TYPE = "obj_contact";

	private final ObjContactPartAddressRepository addressRepository;
	private final CodePartListType addressListType;

	@Autowired
	//@formatter:off
	protected ObjContactRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository,
		final ObjPartNoteRepository noteRepository,
		final ObjContactPartAddressRepository addressRepository
	) {
		super(
			ObjContactRepository.class,
			ObjContact.class,
			ObjContactBase.class,
			ITEM_TYPE,
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
	public ObjContactPartAddressRepository getAddressRepository() {
		return this.addressRepository;
	}

	@Override
	public CodePartListType getAddressListType() {
		return this.addressListType;
	}

	@Override
	public ObjContact doCreate(SessionInfo sessionInfo) {
		return doCreate(sessionInfo, this.dslContext.newRecord(Tables.OBJ_CONTACT));
	}

	@Override
	public void doInitParts(ObjContact obj) {
		super.doInitParts(obj);
		this.addressRepository.init(obj);
	}

	@Override
	public List<ObjContactVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.OBJ_CONTACT_V, Tables.OBJ_CONTACT_V.ID, querySpec);
	}

	@Override
	protected String getCommunityIdField() {
		return "account_id";
	}

	@Override
	public Optional<ObjContact> doLoad(SessionInfo sessionInfo, Integer objId) {
		require(objId != null, "objId not null");
		ObjContactRecord contactRecord = this.dslContext.fetchOne(Tables.OBJ_CONTACT, Tables.OBJ_CONTACT.OBJ_ID.eq(objId));
		if (contactRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, contactRecord);
	}

	@Override
	public void doLoadParts(ObjContact obj) {
		super.doLoadParts(obj);
		this.addressRepository.load(obj);
		((ObjContactBase) obj).loadAddressList(this.addressRepository.getPartList(obj, this.getAddressListType()));
	}

	@Override
	public void doStoreParts(ObjContact obj) {
		super.doStoreParts(obj);
		this.addressRepository.store(obj);
	}

	@Override
	public void changeOwner(List<ObjContact> contacts, ObjUser user) {
		contacts.stream().forEach((contact) -> {
			contact.setOwner(user);
			this.store(contact);
		});
	}

}
