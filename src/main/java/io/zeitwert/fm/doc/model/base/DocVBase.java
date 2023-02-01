package io.zeitwert.fm.doc.model.base;

import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.doc.model.base.DocBase;

public abstract class DocVBase extends DocBase {

	public DocVBase(DocVRepository repository, AggregateState state) {
		super(repository, state);
	}

}
