
package io.zeitwert.ddd.oe.model.base;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.ddd.oe.model.enums.CodeTenantTypeEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;

public abstract class ObjTenantBase extends ObjBase implements ObjTenant {

	protected final EnumProperty<CodeTenantType> tenantType;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> extlKey;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<BigDecimal> inflationRate;
	protected final ReferenceProperty<ObjDocument> logoImage;

	public ObjTenantBase(ObjTenantRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> tenantRecord) {
		super(repository, objRecord, tenantRecord);
		this.tenantType = this.addEnumProperty(this.extnDbRecord(), ObjTenantFields.TENANT_TYPE_ID,
				CodeTenantTypeEnum.class);
		this.name = this.addSimpleProperty(this.extnDbRecord(), ObjTenantFields.NAME);
		this.extlKey = this.addSimpleProperty(this.extnDbRecord(), ObjTenantFields.EXTL_KEY);
		this.description = this.addSimpleProperty(this.extnDbRecord(), ObjTenantFields.DESCRIPTION);
		this.inflationRate = this.addSimpleProperty(this.extnDbRecord(), ObjTenantFields.INFLATION_RATE);
		this.logoImage = this.addReferenceProperty(this.extnDbRecord(), ObjTenantFields.LOGO_IMAGE, ObjDocument.class);
	}

	@Override
	public ObjTenantRepository getRepository() {
		return (ObjTenantRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addLogoImage();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getLogoImageId() == null) {
			this.addLogoImage();
		}
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getExtlKey());
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
	}

	@Override
	public List<ObjUser> getUsers() {
		ObjUserRepository userRepo = (ObjUserRepository) this.getAppContext().getRepository(ObjUser.class);
		return userRepo.getByForeignKey("tenantId", this.getId())
				.stream()
				.map(c -> userRepo.get((Integer) c.get("id")))
				.toList();
	}

	@Override
	public List<ObjAccount> getAccounts() {
		ObjAccountRepository accountRepo = (ObjAccountRepository) this.getAppContext().getRepository(ObjAccount.class);
		return accountRepo.getByForeignKey("tenantId", this.getId()).stream().map(c -> accountRepo.get(c.getId())).toList();
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
