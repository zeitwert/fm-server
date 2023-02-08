
package io.zeitwert.ddd.oe.model.base;

import java.math.BigDecimal;
import java.util.List;

import org.jooq.TableRecord;

import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjTenantRepository;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeTenantType;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.db.tables.records.ObjAccountVRecord;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;

public abstract class ObjTenantBase extends ObjExtnBase implements ObjTenant {

	//@formatter:off
	protected final EnumProperty<CodeTenantType> tenantType = this.addEnumProperty("tenantType", CodeTenantType.class);
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	protected final SimpleProperty<BigDecimal> inflationRate = this.addSimpleProperty("inflationRate", BigDecimal.class);
	protected final ReferenceProperty<ObjDocument> logoImage = this.addReferenceProperty("logoImage", ObjDocument.class);
	//@formatter:on

	public ObjTenantBase(ObjTenantRepository repository, Object state) {
		super(repository, state);
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
				.map(c -> (TableRecord<?>) c)
				.map(c -> userRepo.get((Integer) c.get("id")))
				.toList();
	}

	@Override
	public List<ObjAccount> getAccounts() {
		ObjAccountRepository accountRepo = (ObjAccountRepository) this.getAppContext().getRepository(ObjAccount.class);
		List<ObjAccountVRecord> accountIds = accountRepo.getByForeignKey("tenantId", this.getId());
		return accountIds.stream().map(c -> accountRepo.get(c.getId())).toList();
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
