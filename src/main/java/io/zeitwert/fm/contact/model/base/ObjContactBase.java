
package io.zeitwert.fm.contact.model.base;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.collaboration.model.impl.ItemWithNotesImpl;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.contact.model.ObjContactPartAddress;
import io.zeitwert.fm.contact.model.ObjContactPartAddressRepository;
import io.zeitwert.fm.contact.model.ObjContactRepository;
import io.zeitwert.fm.contact.model.enums.CodeAddressChannel;
import io.zeitwert.fm.contact.model.enums.CodeContactRole;
import io.zeitwert.fm.contact.model.enums.CodeSalutation;
import io.zeitwert.fm.contact.model.enums.CodeTitle;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.impl.ItemWithTasksImpl;
import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class ObjContactBase extends ObjExtnBase implements ObjContact {

	//@formatter:off
	protected final EnumProperty<CodeContactRole> contactRole = this.addEnumProperty("contactRole", CodeContactRole.class);
	protected final EnumProperty<CodeSalutation> salutation = this.addEnumProperty("salutation", CodeSalutation.class);
	protected final EnumProperty<CodeTitle> title = this.addEnumProperty("title", CodeTitle.class);
	protected final SimpleProperty<String> firstName = this.addSimpleProperty("firstName", String.class);
	protected final SimpleProperty<String> lastName = this.addSimpleProperty("lastName", String.class);
	protected final SimpleProperty<LocalDate> birthDate = this.addSimpleProperty("birthDate", LocalDate.class);
	protected final SimpleProperty<String> phone = this.addSimpleProperty("phone", String.class);
	protected final SimpleProperty<String> mobile = this.addSimpleProperty("mobile", String.class);
	protected final SimpleProperty<String> email = this.addSimpleProperty("email", String.class);
	protected final SimpleProperty<String> description = this.addSimpleProperty("description", String.class);
	private final PartListProperty<ObjContactPartAddress> addressList = this.addPartListProperty("addressList", ObjContactPartAddress.class);
	//@formatter:on

	private final ItemWithNotesImpl notes = new ItemWithNotesImpl(this);
	private final ItemWithTasksImpl tasks = new ItemWithTasksImpl(this);

	protected ObjContactBase(ObjContactRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjContactRepository getRepository() {
		return (ObjContactRepository) super.getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjContactPartAddressRepository addressRepo = ObjContactRepository.getAddressRepository();
		this.addressList.loadParts(addressRepo.getParts(this, ObjContactRepository.addressListType()));
	}

	@Override
	public List<ObjNoteVRecord> getNotes() {
		return this.notes.getNotes();
	}

	@Override
	public ObjNote addNote(CodeNoteType noteType) {
		return this.notes.addNote(noteType);
	}

	@Override
	public void removeNote(Integer noteId) {
		this.notes.removeNote(noteId);
	}

	@Override
	public List<DocTaskVRecord> getTasks() {
		return this.tasks.getTasks();
	}

	@Override
	public DocTask addTask() {
		return this.tasks.addTask();
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getFirstName());
		this.addSearchToken(this.getLastName());
		this.addSearchToken(this.getEmail());
		this.addSearchText(this.getEmail().replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "));
		this.addSearchText(this.getDescription());
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
