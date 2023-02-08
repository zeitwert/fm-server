package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.jooq.property.DocPartFields;
import io.zeitwert.jooq.property.DocPartPropertyProviderMixin;

public abstract class DocPartPersistenceProviderBase<D extends Doc, P extends Part<D>>
		extends PartPersistenceProviderBase<D, P>
		implements DocPartPropertyProviderMixin, PartPersistenceProviderMixin<D, P> {

	private static final String DOC_PART_ID_SEQ = "doc_part_id_seq";

	public DocPartPersistenceProviderBase(Class<? extends Part<D>> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
		this.mapProperties();
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(DOC_PART_ID_SEQ).intValue();
	}

	@Override
	public final void doInit(Part<?> part, Integer partId, D doc, Part<?> parent, CodePartListType partListType) {
		super.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(DocPartFields.DOC_ID, doc.getId());
	}

}
