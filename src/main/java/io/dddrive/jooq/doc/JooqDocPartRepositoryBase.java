package io.dddrive.jooq.doc;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPart;
import io.dddrive.jooq.ddd.JooqPartRepositoryBase;

public abstract class JooqDocPartRepositoryBase<D extends Doc, P extends DocPart<D>>
		extends JooqPartRepositoryBase<D, P>
		implements DocPartPropertyProviderMixin {

	protected JooqDocPartRepositoryBase(
			Class<? extends D> aggregateIntfClass,
			Class<? extends DocPart<D>> intfClass,
			Class<? extends DocPart<D>> baseClass,
			String partTypeId) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId);
		this.mapProperties();
	}

}
