
package io.zeitwert.fm.oe.model.base;

import java.util.Set;
import java.util.stream.Collectors;

import io.dddrive.oe.model.ObjTenant;
import io.dddrive.oe.model.base.ObjUserBase;
import io.zeitwert.fm.oe.model.enums.CodeUserRole;
import io.zeitwert.fm.oe.model.enums.CodeUserRoleEnum;
import io.dddrive.oe.service.api.ObjTenantCache;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.ReferenceSetProperty;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;

public abstract class ObjUserFMBase extends ObjUserBase implements ObjUserFM {

	//@formatter:off
	protected final ReferenceProperty<ObjDocument> avatarImage = this.addReferenceProperty("avatarImage", ObjDocument.class);
	protected final SimpleProperty<String> role = this.addSimpleProperty("role", String.class);
	protected final ReferenceSetProperty<ObjTenant> tenantSet = this.addReferenceSetProperty("tenantSet", ObjTenant.class);
	protected final SimpleProperty<Boolean> needPasswordChange = this.addSimpleProperty("needPasswordChange", Boolean.class);
	protected final SimpleProperty<String> password = this.addSimpleProperty("password", String.class);
	//@formatter:on

	public ObjUserFMBase(ObjUserFMRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjUserFMRepository getRepository() {
		return (ObjUserFMRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addAvatarImage();
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
	public boolean isAppAdmin() {
		return this.getRepository().isAppAdmin(this);
	}

	@Override
	public boolean isAdmin() {
		return this.getRepository().isAdmin(this);
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
	public void addTenant(ObjTenant tenant) {
		this.tenantSet.addItem(tenant.getId());
	}

	@Override
	public void clearTenantSet() {
		this.tenantSet.clearItems();
	}

}
