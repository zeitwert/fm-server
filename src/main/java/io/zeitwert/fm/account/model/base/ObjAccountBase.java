
package io.zeitwert.fm.account.model.base;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.account.model.enums.CodeAccountTypeEnum;
import io.zeitwert.fm.account.model.enums.CodeArea;
import io.zeitwert.fm.account.model.enums.CodeAreaEnum;
import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.zeitwert.fm.account.model.enums.CodeClientSegmentEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;

public abstract class ObjAccountBase extends FMObjBase implements ObjAccount {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<String> key;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final EnumProperty<CodeAccountType> accountType;
	protected final EnumProperty<CodeClientSegment> clientSegment;
	protected final EnumProperty<CodeCurrency> referenceCurrency;
	protected final SimpleProperty<BigDecimal> inflationRate;
	protected final ReferenceProperty<ObjDocument> logoImage;
	protected final ReferenceProperty<ObjContact> mainContact;
	protected final EnumSetProperty<CodeArea> areaSet;

	protected ObjAccountBase(ObjAccountRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> accountRecord) {
		super(repository, objRecord);
		this.dbRecord = accountRecord;
		this.key = this.addSimpleProperty(this.dbRecord, ObjAccountFields.KEY);
		this.name = this.addSimpleProperty(this.dbRecord, ObjAccountFields.NAME);
		this.description = this.addSimpleProperty(this.dbRecord, ObjAccountFields.DESCRIPTION);
		this.accountType = this.addEnumProperty(this.dbRecord, ObjAccountFields.ACCOUNT_TYPE_ID,
				CodeAccountTypeEnum.class);
		this.clientSegment = this.addEnumProperty(this.dbRecord, ObjAccountFields.CLIENT_SEGMENT_ID,
				CodeClientSegmentEnum.class);
		this.referenceCurrency = this.addEnumProperty(this.dbRecord, ObjAccountFields.REFERENCE_CURRENCY_ID,
				CodeCurrencyEnum.class);
		this.inflationRate = this.addSimpleProperty(this.dbRecord, ObjAccountFields.INFLATION_RATE);
		this.logoImage = this.addReferenceProperty(this.dbRecord, ObjAccountFields.LOGO_IMAGE, ObjDocument.class);
		this.mainContact = this.addReferenceProperty(this.dbRecord, ObjAccountFields.MAIN_CONTACT_ID, ObjContact.class);
		this.areaSet = this.addEnumSetProperty(this.getRepository().getAreaSetType(), CodeAreaEnum.class);
	}

	@Override
	public ObjAccountRepository getRepository() {
		return (ObjAccountRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjAccountFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjAccountFields.TENANT_ID, tenantId);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addLogoImage();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.areaSet.loadEnums(itemRepo.getParts(this, this.getRepository().getAreaSetType()));
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getLogoImageId() == null) {
			this.addLogoImage();
		}
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
		if (super.account.getId() == null) {
			super.account.setId(this.getId());
			super.doStore();
		}
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getKey());
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public List<ObjContact> getContacts() {
		ObjContactRepository contactRepo = (ObjContactRepository) this.getAppContext().getRepository(ObjContact.class);
		return contactRepo.getByForeignKey("accountId", this.getId()).stream().map(c -> contactRepo.get(c.getId()))
				.toList();
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption(this.getName());
	}

	private void addLogoImage() {
		ObjDocumentRepository documentRepo = (ObjDocumentRepository) this.getAppContext().getRepository(ObjDocument.class);
		ObjDocument image = documentRepo.create(this.getTenantId());
		image.setName("Logo");
		image.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		image.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		image.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("logo"));
		documentRepo.store(image);
		this.logoImage.setId(image.getId());
	}

}
