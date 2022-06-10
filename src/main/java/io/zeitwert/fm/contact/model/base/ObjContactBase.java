
package io.zeitwert.fm.contact.model.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jooq.UpdatableRecord;

import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.enums.CodeContactRole;
import io.zeitwert.fm.contact.model.enums.CodeContactRoleEnum;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeSalutationEnum;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.contact.model.enums.CodeTitleEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class ObjContactBase extends FMObjBase implements ObjContact {

	private final UpdatableRecord<?> dbRecord;

	protected final ReferenceProperty<ObjAccount> account;
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

	private final List<ObjContactPartAddress> addressList = new ArrayList<>();

	protected ObjContactBase(SessionInfo sessionInfo, ObjContactRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> contactRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = contactRecord;
		this.account = this.addReferenceProperty(dbRecord, ObjContactFields.ACCOUNT_ID, ObjAccount.class);
		this.contactRole = this.addEnumProperty(dbRecord, ObjContactFields.CONTACT_ROLE_ID, CodeContactRoleEnum.class);
		this.salutation = this.addEnumProperty(dbRecord, ObjContactFields.SALUTATION_ID, CodeSalutationEnum.class);
		this.title = this.addEnumProperty(dbRecord, ObjContactFields.TITLE_ID, CodeTitleEnum.class);
		this.firstName = this.addSimpleProperty(dbRecord, ObjContactFields.FIRST_NAME);
		this.lastName = this.addSimpleProperty(dbRecord, ObjContactFields.LAST_NAME);
		this.birthDate = this.addSimpleProperty(dbRecord, ObjContactFields.BIRTH_DATE);
		this.phone = this.addSimpleProperty(dbRecord, ObjContactFields.PHONE);
		this.mobile = this.addSimpleProperty(dbRecord, ObjContactFields.MOBILE);
		this.email = this.addSimpleProperty(dbRecord, ObjContactFields.EMAIL);
		this.description = this.addSimpleProperty(dbRecord, ObjContactFields.DESCRIPTION);
	}

	@Override
	public ObjContactRepository getRepository() {
		return (ObjContactRepository) super.getRepository();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjContactFields.OBJ_ID, objId);
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	private Optional<ObjContactPartAddress> getAddress(Integer addressId) {
		return this.addressList.stream().filter(a -> addressId.equals(a.getId())).findAny();
	}

	private void addAddress(ObjContactPartAddress address) {
		this.addressList.add(address);
	}

	private ObjContactPartAddress addAddress(boolean isMailAddress) {
		ObjContactPartAddress address = this.getRepository().getAddressRepository().create(this, null); // TODO implement
		address.setIsMailAddress(isMailAddress);
		this.addAddress(address);
		return address;
	}

	private void removeAddress(Integer addressId) {
		Optional<ObjContactPartAddress> address = this.getAddress(addressId);
		if (address.isPresent()) {
			((PartSPI<?>) address.get()).delete();
			this.addressList.remove(address.get());
		}
	}

	@Override
	public List<ObjContactPartAddress> getMailAddressList() {
		return this.addressList.stream().filter(a -> a.getIsMailAddress()).toList();
	}

	@Override
	public Optional<ObjContactPartAddress> getMailAddress(Integer addressId) {
		return this.getAddress(addressId);
	}

	@Override
	public void clearMailAddressList() {
		this.addressList.removeIf(a -> a.getIsMailAddress());
	}

	@Override
	public ObjContactPartAddress addMailAddress() {
		return this.addAddress(true);
	}

	@Override
	public void removeMailAddress(Integer addressId) {
		this.removeAddress(addressId);
	}

	@Override
	public List<ObjContactPartAddress> getElectronicAddressList() {
		return this.addressList.stream().filter(a -> !a.getIsMailAddress()).toList();
	}

	@Override
	public Optional<ObjContactPartAddress> getElectronicAddress(Integer addressId) {
		return this.getAddress(addressId);
	}

	@Override
	public void clearElectronicAddressList() {
		this.addressList.removeIf(a -> !a.getIsMailAddress());
	}

	@Override
	public ObjContactPartAddress addElectronicAddress() {
		return this.addAddress(true);
	}

	@Override
	public void removeElectronicAddress(Integer addressId) {
		this.removeAddress(addressId);
	}

	public void loadAddressList(List<ObjContactPartAddress> addresses) {
		this.addressList.clear();
		addresses.forEach(t -> this.addAddress(t));
	}

	@Override
	protected void doCalcAll() {
		this.calcCaption();
	}

	protected void calcCaption() {
		this.caption.setValue((this.getFirstName() + " " + this.getLastName()).trim());
	}

}
