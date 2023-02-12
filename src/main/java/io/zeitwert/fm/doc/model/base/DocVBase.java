package io.zeitwert.fm.doc.model.base;

import io.dddrive.doc.model.base.DocBase;
import io.zeitwert.fm.doc.model.DocVRepository;

public abstract class DocVBase extends DocBase {

	public DocVBase(DocVRepository repository, Object state) {
		super(repository, state);
	}

}
