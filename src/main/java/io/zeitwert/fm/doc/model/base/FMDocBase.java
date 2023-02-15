package io.zeitwert.fm.doc.model.base;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocRepository;
import io.dddrive.doc.model.base.DocExtnBase;
import io.dddrive.property.model.SimpleProperty;

public abstract class FMDocBase extends DocExtnBase {

	//@formatter:off
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccountId", Integer.class);
	//@formatter:on

	protected FMDocBase(DocRepository<? extends Doc, ? extends Object> repository, Object state) {
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
