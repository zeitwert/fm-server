
package fm.comunas.fm.account.model.base;

import java.util.Collection;
import java.util.List;

import org.jooq.UpdatableRecord;

import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.account.model.ObjAccountRepository;
import fm.comunas.fm.account.model.enums.CodeClientSegment;
import fm.comunas.fm.account.model.enums.CodeClientSegmentEnum;
import fm.comunas.fm.account.model.enums.CodeAccountType;
import fm.comunas.fm.account.model.enums.CodeAccountTypeEnum;
import fm.comunas.fm.common.model.enums.CodeArea;
import fm.comunas.fm.common.model.enums.CodeAreaEnum;
import fm.comunas.fm.contact.model.ObjContact;
import fm.comunas.fm.contact.model.ObjContactRepository;
import fm.comunas.fm.obj.model.base.FMObjBase;
import fm.comunas.ddd.common.model.enums.CodeCurrency;
import fm.comunas.ddd.common.model.enums.CodeCurrencyEnum;
import fm.comunas.ddd.obj.model.ObjPartItem;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.EnumProperty;
import fm.comunas.ddd.property.model.EnumSetProperty;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;

public abstract class ObjAccountBase extends FMObjBase implements ObjAccount {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final EnumProperty<CodeAccountType> accountType;
	protected final EnumProperty<CodeClientSegment> clientSegment;
	protected final EnumProperty<CodeCurrency> referenceCurrency;
	protected final ReferenceProperty<ObjContact> mainContact;
	protected final EnumSetProperty<CodeArea> areaSet;

	protected ObjAccountBase(SessionInfo sessionInfo, ObjAccountRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> accountRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = accountRecord;
		this.name = this.addSimpleProperty(dbRecord, ObjAccountFields.NAME);
		this.description = this.addSimpleProperty(dbRecord, ObjAccountFields.DESCRIPTION);
		this.accountType = this.addEnumProperty(dbRecord, ObjAccountFields.ACCOUNT_TYPE_ID,
				CodeAccountTypeEnum.class);
		this.clientSegment = this.addEnumProperty(dbRecord, ObjAccountFields.CLIENT_SEGMENT_ID,
				CodeClientSegmentEnum.class);
		this.referenceCurrency = this.addEnumProperty(dbRecord, ObjAccountFields.REFERENCE_CURRENCY_ID,
				CodeCurrencyEnum.class);
		this.mainContact = this.addReferenceProperty(dbRecord, ObjAccountFields.MAIN_CONTACT_ID, ObjContact.class);
		this.areaSet = this.addEnumSetProperty(this.getRepository().getAreaSetType(), CodeAreaEnum.class);
	}

	@Override
	public ObjAccountRepository getRepository() {
		return (ObjAccountRepository) super.getRepository();
	}

	public abstract void loadAreaSet(Collection<ObjPartItem> areaSet);

	@Override
	public void doInit(Integer objId, Integer tenantId, Integer userId) {
		super.doInit(objId, tenantId, userId);
		this.dbRecord.setValue(ObjAccountFields.OBJ_ID, objId);
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public void doStore(Integer userId) {
		super.doStore(userId);
		this.dbRecord.store();
	}

	@Override
	public List<ObjContact> getContacts() {
		SessionInfo sessionInfo = this.getMeta().getSessionInfo();
		ObjContactRepository contactRepo = (ObjContactRepository) this.getAppContext().getRepository(ObjContact.class);
		return contactRepo.getByForeignKey(sessionInfo, "accountId", this.getId()).stream()
				.map(c -> contactRepo.get(sessionInfo, c.getId()).get()).toList();
	}

	@Override
	protected void doCalcAll() {
		this.calcCaption();
	}

	private void calcCaption() {
		this.caption.setValue(this.getName());
	}

}
