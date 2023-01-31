package io.zeitwert.fm.doc.model.base;

import io.zeitwert.fm.doc.model.DocVRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;

import org.jooq.UpdatableRecord;

public abstract class DocVBase extends DocBase {

	public DocVBase(DocVRepository repository, UpdatableRecord<?> docRecord, UpdatableRecord<?> extnRecord) {
		super(repository, docRecord, extnRecord);
	}

}
