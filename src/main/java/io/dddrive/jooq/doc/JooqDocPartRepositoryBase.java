package io.dddrive.jooq.doc;

import org.jooq.UpdatableRecord;

import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPart;
import io.dddrive.jooq.ddd.JooqPartRepositoryBase;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.fm.doc.model.base.DocPartFields;

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

	@Override
	public final void doInit(Part<?> part, Integer partId, D doc, Part<?> parent, CodePartListType partListType) {
		this.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(DocPartFields.DOC_ID, doc.getId());
	}

}
