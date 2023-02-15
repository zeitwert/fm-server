package io.zeitwert.fm.doc.model.base;

import io.dddrive.doc.model.base.DocBase;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.doc.model.DocVRepository;

public abstract class DocVBase extends DocBase {

	//@formatter:off
	protected final SimpleProperty<Integer> accountId = this.addSimpleProperty("accountId", Integer.class);
	//@formatter:on

	public DocVBase(DocVRepository repository, Object state) {
		super(repository, state);
	}

}
