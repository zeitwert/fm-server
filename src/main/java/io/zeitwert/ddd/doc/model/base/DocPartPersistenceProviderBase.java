package io.zeitwert.ddd.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.jooq.base.PartPersistenceProviderBase;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public abstract class DocPartPersistenceProviderBase<D extends Doc, P extends Part<D>>
		extends PartPersistenceProviderBase<D, P> {

	private static final String DOC_PART_ID_SEQ = "doc_part_id_seq";

	public DocPartPersistenceProviderBase(
			final Class<? extends D> docIntfClass,
			Class<? extends PartRepository<D, P>> repoIntfClass,
			Class<? extends Part<D>> baseClass,
			DSLContext dslContext) {
		super(docIntfClass, repoIntfClass, baseClass, dslContext);
		this.mapField("docId", BASE, "doc_id", Integer.class);
	}

	@Override
	public Class<?> getEntityClass() { // TODO: remove here
		return null;
	}

	@Override
	public Integer nextPartId() {
		return this.getDSLContext().nextval(DOC_PART_ID_SEQ).intValue();
	}

	@Override
	public final void doInit(Part<?> part, Integer partId, D doc, Part<?> parent, CodePartListType partListType) {
		super.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(DocPartFields.DOC_ID, doc.getId());
	}

}
