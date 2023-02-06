
package io.zeitwert.fm.dms.model.base;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zeitwert.ddd.obj.model.base.ObjExtnBase;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;
import io.zeitwert.fm.collaboration.model.impl.ItemWithNotesImpl;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;
import io.zeitwert.fm.task.model.impl.ItemWithTasksImpl;

public abstract class ObjDocumentBase extends ObjExtnBase implements ObjDocument {

	protected static final Logger logger = LoggerFactory.getLogger(ObjDocumentBase.class);

	//@formatter:off
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final EnumProperty<CodeDocumentKind> documentKind = this.addEnumProperty("documentKind", CodeDocumentKind.class);
	protected final EnumProperty<CodeDocumentCategory> documentCategory = this.addEnumProperty("documentCategory", CodeDocumentCategory.class);
	protected final ReferenceProperty<ObjDocument> templateDocument = this.addReferenceProperty("templateDocument", ObjDocument.class);
	protected final EnumProperty<CodeContentKind> contentKind = this.addEnumProperty("contentKind", CodeContentKind.class);
	//@formatter:on

	private final ItemWithNotesImpl notes = new ItemWithNotesImpl(this);
	private final ItemWithTasksImpl tasks = new ItemWithTasksImpl(this);

	protected CodeContentType contentType;
	protected byte[] content;

	protected ObjDocumentBase(ObjDocumentRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjDocumentRepository getRepository() {
		return (ObjDocumentRepository) super.getRepository();
	}

	@Override
	public void doAfterLoad() {
		super.doAfterLoad();
		this.loadContent();
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
	public CodeContentType getContentType() {
		return this.contentType;
	}

	@Override
	public byte[] getContent() {
		return this.content;
	}

	@Override
	public void storeContent(CodeContentType contentType, byte[] content) {
		this.getRepository().storeContent(this.getRequestContext(), this, contentType, content);
		this.contentType = contentType;
		this.content = content;
		this.calcAll();
	}

	@Override
	public void doCalcSearch() {
		this.addSearchText(this.getName());
	}

	@Override
	public void doAfterStore() {
		super.doAfterStore();
		this.loadContent();
	}

	private void loadContent() {
		this.contentType = this.getRepository().getContentType(this);
		if (this.contentType != null) {
			this.content = this.getRepository().getContent(this);
		}
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	protected void calcCaption() {
		this.setCaption(this.getName());
	}

}
