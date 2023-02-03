
package io.zeitwert.ddd.oe.model.base;

import java.util.Set;
import java.util.stream.Collectors;

import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;

public abstract class ObjUserBase extends ObjExtnBase implements ObjUser {

	//@formatter:off
	protected final SimpleProperty<String> email = this.addSimpleProperty("email", String.class);
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	protected final ReferenceProperty<ObjDocument> avatarImage = this.addReferenceProperty("avatarImage", ObjDocument.class);
	protected final SimpleProperty<String> role = this.addSimpleProperty("role", String.class);
	protected final ReferenceSetProperty<ObjTenant> tenantSet = this.addReferenceSetProperty("tenantSet", ObjTenant.class);
	protected final SimpleProperty<Boolean> needPasswordChange = this.addSimpleProperty("needPasswordChange", Boolean.class);
	protected final SimpleProperty<String> password = this.addSimpleProperty("password", String.class);
	//@formatter:on

	public ObjUserBase(ObjUserRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjUserRepository getRepository() {
		return (ObjUserRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addAvatarImage();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = ObjRepository.getItemRepository();
		this.tenantSet.loadReferences(itemRepo.getParts(this, ObjUserRepository.tenantListType()));
	}

	@Override
	public CodeUserRole getRole() {
		return CodeUserRoleEnum.getUserRole(this.role.getValue());
	}

	@Override
	public boolean hasRole(CodeUserRole role) {
		return this.getRole() == role;
	}

	@Override
	public void setRole(CodeUserRole role) {
		this.role.setValue(role == null ? null : role.getId());
	}

	@Override
	public void setPassword(String password) {
		this.password.setValue(this.getRepository().getPasswordEncoder().encode(password));
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getAvatarImageId() == null) {
			this.addAvatarImage();
		}
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getEmail());
		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "));
		this.addSearchText(this.getName());
		this.addSearchText(this.getDescription());
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.tenantSet)) {
			return ObjRepository.getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
	}

	private void addAvatarImage() {
		ObjDocumentRepository documentRepo = (ObjDocumentRepository) this.getAppContext().getRepository(ObjDocument.class);
		ObjDocument image = documentRepo.create(this.getTenantId());
		image.setName("Avatar");
		image.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		image.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		image.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("avatar"));
		documentRepo.store(image);
		this.avatarImage.setId(image.getId());
	}

	@Override
	public Set<ObjTenant> getTenantSet() {
		ObjTenantCache tenantCache = this.getRepository().getTenantCache();
		return this.tenantSet.getItems()
				.stream()
				.map(itemId -> tenantCache.get(itemId))
				.collect(Collectors.toSet());
	}

	@Override
	public void clearTenantSet() {
		this.tenantSet.clearItems();
	}

}
