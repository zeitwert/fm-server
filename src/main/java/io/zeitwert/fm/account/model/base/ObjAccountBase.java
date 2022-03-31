
package io.zeitwert.fm.account.model.base;

import java.util.Collection;
import java.util.List;

import org.jooq.UpdatableRecord;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.zeitwert.fm.account.model.enums.CodeClientSegmentEnum;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.common.model.enums.CodeArea;
import io.zeitwert.fm.common.model.enums.CodeAreaEnum;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.ddd.common.model.enums.CodeCurrency;
import io.zeitwert.ddd.common.model.enums.CodeCurrencyEnum;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;

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
				.map(c -> contactRepo.get(sessionInfo, c.getId())).toList();
	}

	@Override
	protected void doCalcAll() {
		this.calcCaption();
	}

	private void calcCaption() {
		this.caption.setValue(this.getName());
	}

	@Override
	public void beforeStore() {
		super.beforeStore();
		this.areaSet.beforeStore();
	}

}
