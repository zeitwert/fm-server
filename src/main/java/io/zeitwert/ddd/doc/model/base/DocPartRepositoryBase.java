package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPart;
import io.zeitwert.ddd.doc.model.DocPartRepository;
import io.zeitwert.ddd.part.model.base.PartRepositoryBase;

public abstract class DocPartRepositoryBase<D extends Doc, P extends DocPart<D>> extends PartRepositoryBase<D, P>
		implements DocPartRepository<D, P> {

	protected DocPartRepositoryBase(
			Class<? extends D> aggregateIntfClass,
			Class<? extends DocPart<D>> intfClass,
			Class<? extends DocPart<D>> baseClass,
			String partTypeId,
			AppContext appContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext);
	}

}
