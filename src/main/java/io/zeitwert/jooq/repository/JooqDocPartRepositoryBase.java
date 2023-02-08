package io.zeitwert.jooq.repository;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPart;
import io.zeitwert.ddd.doc.model.base.DocPartFields;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.jooq.property.DocPartPropertyProviderMixin;

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
