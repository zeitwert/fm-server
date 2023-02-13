package io.zeitwert.fm.doc.model.base;

import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPart;
import io.dddrive.jooq.doc.JooqDocPartRepositoryBase;

public abstract class FMDocPartRepositoryBase<D extends Doc, P extends DocPart<D>>
		extends JooqDocPartRepositoryBase<D, P> {

	private static final String DOC_PART_ID_SEQ = "doc_part_id_seq";

	protected FMDocPartRepositoryBase(
			Class<? extends D> aggregateIntfClass,
			Class<? extends DocPart<D>> intfClass,
			Class<? extends DocPart<D>> baseClass,
			String partTypeId) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId);
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(DOC_PART_ID_SEQ).intValue();
	}

}
