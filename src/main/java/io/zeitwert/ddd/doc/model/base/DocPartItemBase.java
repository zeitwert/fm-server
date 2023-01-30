package io.zeitwert.ddd.doc.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class DocPartItemBase extends DocPartBase<Doc> implements DocPartItem {

	protected final SimpleProperty<String> itemId;

	public DocPartItemBase(PartRepository<Doc, ?> repository, Doc doc, UpdatableRecord<?> dbRecord) {
		super(repository, doc, dbRecord);
		this.itemId = this.addSimpleProperty(dbRecord, DocPartItemFields.ITEM_ID);
	}

	@Override
	public String toString() {
		return "DocPartItem["
				+ this.getAggregate().getId() + "|"
				+ this.getParentPartId() + "|"
				+ this.getPartListTypeId() + "]: "
				+ this.getItemId();
	}

}
