
package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPart;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import org.jooq.UpdatableRecord;

public abstract class DocPartBase<D extends Doc> extends PartBase<D> implements DocPart<D> {

	protected DocPartBase(PartRepository<D, ?> repository, D aggregate, UpdatableRecord<?> dbRecord) {
		super(repository, aggregate, dbRecord);
	}

	@Override
	public final void doInit(Integer partId, D doc, Part<?> parent, CodePartListType partListType) {
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		if (partId != null) {
			dbRecord.setValue(DocPartFields.ID, partId);
		}
		dbRecord.setValue(DocPartFields.DOC_ID, doc.getId());
		dbRecord.setValue(DocPartFields.PARENT_PART_ID, parent != null ? parent.getId() : null);
		dbRecord.setValue(DocPartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
