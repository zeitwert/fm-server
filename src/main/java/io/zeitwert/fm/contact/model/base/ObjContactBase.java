
package io.zeitwert.fm.contact.model.base;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.jooq.UpdatableRecord;

import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.zeitwert.fm.contact.model.enums.CodeContactRole;
import io.zeitwert.fm.contact.model.enums.CodeContactRoleEnum;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class ObjContactBase extends FMObjBase implements ObjContact {

	private final UpdatableRecord<?> dbRecord;

	protected final EnumProperty<CodeContactRole> contactRole;
	protected final EnumProperty<CodeSalutation> salutation;
	protected final EnumProperty<CodeTitle> title;
	protected final SimpleProperty<String> firstName;
	protected final SimpleProperty<String> lastName;
	protected final SimpleProperty<LocalDate> birthDate;
	protected final SimpleProperty<String> phone;
	protected final SimpleProperty<String> mobile;
	protected final SimpleProperty<String> email;
	protected final SimpleProperty<String> description;

	private final PartListProperty<ObjContactPartAddress> addressList;

	protected ObjContactBase(ObjContactRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> contactRecord) {
		super(repository, objRecord);
		this.dbRecord = contactRecord;
		this.contactRole = this.addEnumProperty(this.dbRecord, ObjContactFields.CONTACT_ROLE_ID, CodeContactRoleEnum.class);
		this.salutation = this.addEnumProperty(this.dbRecord, ObjContactFields.SALUTATION_ID, CodeSalutationEnum.class);
		this.title = this.addEnumProperty(this.dbRecord, ObjContactFields.TITLE_ID, CodeTitleEnum.class);
		this.firstName = this.addSimpleProperty(this.dbRecord, ObjContactFields.FIRST_NAME);
		this.lastName = this.addSimpleProperty(this.dbRecord, ObjContactFields.LAST_NAME);
		this.birthDate = this.addSimpleProperty(this.dbRecord, ObjContactFields.BIRTH_DATE);
		this.phone = this.addSimpleProperty(this.dbRecord, ObjContactFields.PHONE);
		this.mobile = this.addSimpleProperty(this.dbRecord, ObjContactFields.MOBILE);
		this.email = this.addSimpleProperty(this.dbRecord, ObjContactFields.EMAIL);
		this.description = this.addSimpleProperty(this.dbRecord, ObjContactFields.DESCRIPTION);
		this.addressList = this.addPartListProperty(this.getRepository().getAddressListType());
	}

	@Override
	public ObjContactRepository getRepository() {
		return (ObjContactRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjContactFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjContactFields.TENANT_ID, tenantId);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjContactPartAddressRepository addressRepo = this.getRepository().getAddressRepository();
		this.addressList.loadParts(addressRepo.getParts(this, this.getRepository().getAddressListType()));
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.dbRecord.setValue(ObjContactFields.ACCOUNT_ID, id);
	}

	private ObjContactPartAddress addAddress(CodeAddressChannel addressChannel) {
		ObjContactPartAddress address = this.addressList.addPart();
		address.setAddressChannel(addressChannel);
		return address;
	}

	private void removeAddress(Integer addressId) {
		this.addressList.removePart(addressId);
	}

	@Override
	public List<ObjContactPartAddress> getMailAddressList() {
		return this.addressList.getParts().stream().filter(a -> a.getIsMailAddress()).toList();
	}

	@Override
	public Optional<ObjContactPartAddress> getMailAddress(Integer addressId) {
		return Optional.of(this.addressList.getPartById(addressId));
	}

	@Override
	public void clearMailAddressList() {
		this.getMailAddressList().forEach(a -> this.addressList.removePart(a.getId()));
	}

	@Override
	public ObjContactPartAddress addMailAddress() {
		return this.addAddress(null);
	}

	@Override
	public void removeMailAddress(Integer addressId) {
		this.removeAddress(addressId);
	}

	@Override
	public List<ObjContactPartAddress> getElectronicAddressList() {
		return this.addressList.getParts().stream().filter(a -> !a.getIsMailAddress()).toList();
	}

	@Override
	public Optional<ObjContactPartAddress> getElectronicAddress(Integer addressId) {
		return Optional.of(this.addressList.getPartById(addressId));
	}

	@Override
	public void clearElectronicAddressList() {
		this.getElectronicAddressList().forEach(a -> this.addressList.removePart(a.getId()));
	}

	@Override
	public ObjContactPartAddress addElectronicAddress() {
		return this.addAddress(null);
	}

	@Override
	public void removeElectronicAddress(Integer addressId) {
		this.removeAddress(addressId);
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	protected void calcCaption() {
		this.setCaption((this.getFirstName() + " " + this.getLastName()).trim());
	}

}
