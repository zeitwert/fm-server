
package io.zeitwert.fm.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.doc.model.base.DocBase;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;

import org.jooq.Record;
import org.jooq.UpdatableRecord;

public abstract class FMDocBase extends DocBase implements FMDoc {

	protected FMDocBase(SessionInfo sessionInfo, DocRepository<? extends Doc, ? extends Record> repository,
			UpdatableRecord<?> docRecord) {
		super(sessionInfo, repository, docRecord);
	}

	@Override
	@SuppressWarnings("unchecked")
	public FMDocRepository<? extends FMDoc, ? extends Record> getRepository() {
		return (FMDocRepository<? extends FMDoc, ? extends Record>) super.getRepository();
	}

}
