package io.zeitwert.fm.obj.model.base;

import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjRepository;
import io.dddrive.obj.model.base.ObjExtnBase;
import io.dddrive.property.model.SimpleProperty;
// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
// import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.obj.model.FMObjRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMObjBase extends ObjExtnBase {

	//@formatter:off
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
	//@formatter:on

	protected FMObjBase(ObjRepository<? extends Obj, ? extends Object> repository, Object state) {
		super(repository, state);
	}

	@Override
	public FMObjRepository<? extends Obj, ? extends Object> getRepository() {
		return (FMObjRepository<? extends Obj, ? extends Object>) super.getRepository();
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
