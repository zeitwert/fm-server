package io.zeitwert.ddd.doc.model.base;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class DocPartItemBase extends DocPartBase<Doc> implements DocPartItem {

	protected final SimpleProperty<String> itemId = this.addSimpleProperty("itemId", String.class);

	public DocPartItemBase(PartRepository<Doc, ?> repository, Doc doc, Object state) {
		super(repository, doc, state);
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
