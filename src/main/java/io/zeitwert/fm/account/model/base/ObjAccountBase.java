
package io.zeitwert.fm.account.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.math.BigDecimal;
import java.util.List;

import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.ObjAccountRepository;
import io.zeitwert.fm.account.model.enums.CodeAccountType;
import io.zeitwert.fm.account.model.enums.CodeClientSegment;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.db.tables.records.ObjContactVRecord;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;

public abstract class ObjAccountBase extends FMObjBase implements ObjAccount {

	//@formatter:off
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	protected final EnumProperty<CodeAccountType> accountType = this.addEnumProperty("accountType", CodeAccountType.class);
	protected final EnumProperty<CodeClientSegment> clientSegment = this.addEnumProperty("clientSegment", CodeClientSegment.class);
	protected final EnumProperty<CodeCurrency> referenceCurrency = this.addEnumProperty("referenceCurrency", CodeCurrency.class);
	protected final SimpleProperty<BigDecimal> inflationRate = this.addSimpleProperty("inflationRate", BigDecimal.class);
	protected final ReferenceProperty<ObjDocument> logoImage= this.addReferenceProperty("logoImage", ObjDocument.class);
	protected final ReferenceProperty<ObjContact> mainContact= this.addReferenceProperty("mainContact", ObjContact.class);
	//@formatter:on

	protected ObjAccountBase(ObjAccountRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjAccountRepository getRepository() {
		return (ObjAccountRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		assertThis(this.getId() != null, "id must not be null after create");
		super.account.setId(this.getId());
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
	public void doAfterStore() {
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
	public List<ObjContact> getContacts() {
		ObjContactRepository contactRepo = (ObjContactRepository) this.getAppContext().getRepository(ObjContact.class);
		List<ObjContactVRecord> contactIds = contactRepo.getByForeignKey("accountId", this.getId());
		return contactIds.stream().map(c -> contactRepo.get(c.getId())).toList();
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.caption.setValue(this.getName());
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
