
package fm.comunas.ddd.doc.model.base;

import fm.comunas.ddd.doc.model.Doc;
import fm.comunas.ddd.doc.model.DocPart;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.part.model.base.PartBase;
import fm.comunas.ddd.property.model.enums.CodePartListType;

import org.jooq.UpdatableRecord;

public abstract class DocPartBase<D extends Doc> extends PartBase<D> implements DocPart<D> {

	protected DocPartBase(D aggregate, UpdatableRecord<?> dbRecord) {
		super(aggregate, dbRecord);
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
