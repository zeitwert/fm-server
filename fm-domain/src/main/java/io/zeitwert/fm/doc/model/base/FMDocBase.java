package io.zeitwert.fm.doc.model.base;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocRepository;
import io.dddrive.doc.model.base.DocExtnBase;
import io.dddrive.property.model.SimpleProperty;
// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
// import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMDocBase extends DocExtnBase {

	//@formatter:off
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
	//@formatter:on

	protected FMDocBase(DocRepository<? extends Doc, ? extends Object> repository, Object state) {
		super(repository, state);
	}

	@Override
	public FMDocRepository<? extends Doc, ? extends Object> getRepository() {
		return (FMDocRepository<? extends Doc, ? extends Object>) super.getRepository();
	}

	// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
	// public ObjNoteRepository noteRepository() {
	// 	return this.getRepository().getNoteRepository();
	// }

	public DocTaskRepository taskRepository() {
		return this.getRepository().getTaskRepository();
	}

	public final Integer getAccountId() {
		return this.accountId.getValue();
	}

	public final void setAccountId(Integer id) {
		this.accountId.setValue(id);
		this.extnAccountId.setValue(id);
	}

}
