package io.zeitwert.fm.obj.model.base;

import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjRepository;
import io.dddrive.obj.model.base.ObjExtnBase;
import io.dddrive.property.model.SimpleProperty;

public abstract class FMObjBase extends ObjExtnBase {

	//@formatter:off
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
	//@formatter:on

	protected FMObjBase(ObjRepository<? extends Obj, ? extends Object> repository, Object state) {
		super(repository, state);
	}

	public final Integer getAccountId() {
		return this.accountId.getValue();
	}

	public final void setAccountId(Integer id) {
		this.accountId.setValue(id);
		this.extnAccountId.setValue(id);
	}

}
