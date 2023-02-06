
package io.zeitwert.fm.test.model.base;

import io.zeitwert.fm.account.model.ItemWithAccount;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.collaboration.model.impl.ItemWithNotesImpl;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.impl.ItemWithTasksImpl;
import io.zeitwert.fm.test.model.DocTest;
import io.zeitwert.fm.test.model.DocTestRepository;
import io.zeitwert.fm.test.model.ObjTest;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.base.DocExtnBase;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStageEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.jooq.JSON;

public abstract class DocTestBase extends DocExtnBase implements DocTest {

	//@formatter:off
	protected final SimpleProperty<String> shortText = this.addSimpleProperty("shortText", String.class);
	protected final SimpleProperty<String> longText = this.addSimpleProperty("longText", String.class);
	protected final SimpleProperty<LocalDate> date = this.addSimpleProperty("date", LocalDate.class);
	protected final SimpleProperty<Integer> int_ = this.addSimpleProperty("int", Integer.class);
	protected final SimpleProperty<Boolean> isDone = this.addSimpleProperty("isDone", Boolean.class);
	protected final SimpleProperty<JSON> json = this.addSimpleProperty("json", JSON.class);
	protected final SimpleProperty<BigDecimal> nr = this.addSimpleProperty("nr", BigDecimal.class);
	protected final EnumProperty<CodeCountry> country = this.addEnumProperty("country", CodeCountry.class);
	protected final ReferenceProperty<ObjTest> refObj = this.addReferenceProperty("refObj", ObjTest.class);
	protected final ReferenceProperty<DocTest> refDoc = this.addReferenceProperty("refDoc", DocTest.class);
	protected final EnumSetProperty<CodeCountry> countrySet = this.addEnumSetProperty("countrySet", CodeCountry.class);
	// protected final PartListProperty<DocTestPartNode> nodeList;
	//@formatter:on

	private final ItemWithNotesImpl notes = new ItemWithNotesImpl(this);
	private final ItemWithTasksImpl tasks = new ItemWithTasksImpl(this);

	protected DocTestBase(DocTestRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public DocTestRepository getRepository() {
		return (DocTestRepository) super.getRepository();
	}

	@Override
	public void doInitWorkflow() {
		CodeCaseStage initStage = CodeCaseStageEnum.getCaseStage("test.new");
		this.doInitWorkflow("test", initStage);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartItemRepository itemRepo = DocRepository.getItemRepository();
		this.countrySet.loadEnums(itemRepo.getParts(this, DocTestRepository.countrySetType()));
		// ObjTestPartNodeRepository nodeRepo =
		// this.getRepository().getNodeRepository();
		// this.nodeList.loadParts(nodeRepo.getParts(this,
		// this.getRepository().getNodeListType()));
	}

	@Override
	public final ObjAccount getAccount() {
		return ItemWithAccount.getAccountCache().get(this.getAccountId());
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
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.countrySet)) {
			return DocRepository.getItemRepository().create(this, partListType);
		}
		// if (property.equals(this.nodeList)) {
		// return (P) this.getRepository().getNodeRepository().create(this,
		// partListType);
		// }
		return super.addPart(property, partListType);
	}

	@Override
	public String getJson() {
		return this.json.getValue() == null ? null : this.json.getValue().toString();
	}

	@Override
	public void setJson(String json) {
		this.json.setValue(json == null ? null : JSON.valueOf(json));
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption(
				"[" + this.getString(this.getShortText()) + ", " + this.getString(this.getLongText()) + "]"
						+ (this.getRefObjId() == null ? "" : " (RefObj:" + this.getString(this.getRefObj().getCaption()) + ")")
						+ (this.getRefDocId() == null ? "" : " (RefDoc:" + this.getString(this.getRefDoc().getCaption()) + ")"));
	}

	private String getString(String s) {
		return s == null ? "" : s;
	}

}
