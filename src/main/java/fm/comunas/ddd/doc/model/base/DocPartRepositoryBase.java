package fm.comunas.ddd.doc.model.base;

import org.jooq.DSLContext;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocPart;
import fm.comunas.ddd.doc.model.DocPartRepository;
import fm.comunas.ddd.part.model.base.PartRepositoryBase;

public abstract class DocPartRepositoryBase<D extends Doc, P extends DocPart<D>> extends PartRepositoryBase<D, P>
		implements DocPartRepository<D, P> {

	private final String DOC_PART_ID_SEQ = "doc_part_id_seq";

	//@formatter:off
	protected DocPartRepositoryBase(
		final Class<? extends D> aggregateIntfClass,
		final Class<? extends DocPart<D>> intfClass,
		final Class<? extends DocPart<D>> baseClass,
		final String partTypeId,
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
	}
	//@formatter:on

	@Override
	public Integer nextPartId() {
		return this.dslContext.nextval(DOC_PART_ID_SEQ).intValue();
	}

}
