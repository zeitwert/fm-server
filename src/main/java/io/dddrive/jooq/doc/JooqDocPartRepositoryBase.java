package io.dddrive.jooq.doc;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPart;
import io.dddrive.jooq.ddd.JooqPartRepositoryBase;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

public abstract class JooqDocPartRepositoryBase<D extends Doc, P extends DocPart<D>>
		extends JooqPartRepositoryBase<D, P>
		implements DocPartPropertyProviderMixin {

	private static final String DOC_PART_ID_SEQ = "doc_part_id_seq";

	protected JooqDocPartRepositoryBase(
			Class<? extends D> aggregateIntfClass,
			Class<? extends DocPart<D>> intfClass,
			Class<? extends DocPart<D>> baseClass,
			String partTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
		this.mapProperties();
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(DOC_PART_ID_SEQ).intValue();
	}

	@Override
	public final void doInit(Part<?> part, Integer partId, D doc, Part<?> parent, CodePartListType partListType) {
		this.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(DocPartFields.DOC_ID, doc.getId());
	}

}
