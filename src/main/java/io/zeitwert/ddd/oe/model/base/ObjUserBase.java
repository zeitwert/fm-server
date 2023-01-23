
package io.zeitwert.ddd.oe.model.base;

import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.oe.model.ObjTenant;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.oe.model.ObjUserRepository;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.ddd.oe.model.enums.CodeUserRoleEnum;
import io.zeitwert.ddd.oe.service.api.ObjTenantCache;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.ItemSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;

public abstract class ObjUserBase extends ObjBase implements ObjUser {

	protected final SimpleProperty<String> email;
	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final ReferenceProperty<ObjDocument> avatarImage;
	protected final SimpleProperty<String> role;
	protected final ItemSetProperty<ObjPartItem> tenantSet;
	protected final SimpleProperty<Boolean> needPasswordChange;
	protected final SimpleProperty<String> password;

	private final UpdatableRecord<?> dbRecord;

	public ObjUserBase(ObjUserRepository repository, UpdatableRecord<?> objRecord, UpdatableRecord<?> userRecord) {
		super(repository, objRecord);
		this.dbRecord = userRecord;
		this.email = this.addSimpleProperty(this.dbRecord, ObjUserFields.EMAIL);
		this.name = this.addSimpleProperty(this.dbRecord, ObjUserFields.NAME);
		this.description = this.addSimpleProperty(this.dbRecord, ObjUserFields.DESCRIPTION);
		this.avatarImage = this.addReferenceProperty(this.dbRecord, ObjUserFields.AVATAR_IMAGE, ObjDocument.class);
		this.role = this.addSimpleProperty(this.dbRecord, ObjUserFields.ROLE_LIST);
		this.tenantSet = this.addItemSetProperty(this.getRepository().getTenantSetType());
		this.needPasswordChange = this.addSimpleProperty(this.dbRecord, ObjUserFields.NEED_PASSWORD_CHANGE);
		this.password = this.addSimpleProperty(this.dbRecord, ObjUserFields.PASSWORD);
	}

	@Override
	public ObjUserRepository getRepository() {
		return (ObjUserRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjTenantFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjTenantFields.TENANT_ID, tenantId);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addAvatarImage();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.tenantSet.loadItems(itemRepo.getParts(this, this.getRepository().getTenantSetType()));
	}

	@Override
	public CodeUserRole getRole() {
		return CodeUserRoleEnum.getUserRole(this.dbRecord.getValue(ObjUserFields.ROLE_LIST));
	}

	@Override
	public boolean hasRole(CodeUserRole role) {
		return this.getRole() == role;
	}

	@Override
	public void setRole(CodeUserRole role) {
		this.dbRecord.setValue(ObjUserFields.ROLE_LIST, role == null ? null : role.getId());
	}

	@Override
	public void setPassword(String password) {
		this.dbRecord.setValue(ObjUserFields.PASSWORD, this.getRepository().getPasswordEncoder().encode(password));
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getAvatarImageId() == null) {
			this.addAvatarImage();
		}
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.tenantSet) {
			return (P) this.getRepository().getItemRepository().create(this, partListType);
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
				.map(itemId -> tenantCache.get(Integer.parseInt(itemId)))
				.collect(Collectors.toSet());
	}

	@Override
	public void clearTenantSet() {
		this.tenantSet.clearItems();
	}

	@Override
	public void addTenant(ObjTenant tenant) {
		this.tenantSet.addItem(tenant.getId().toString());
	}

	@Override
	public void removeTenant(ObjTenant tenant) {
		this.tenantSet.removeItem(tenant.getId().toString());
	}

}
