package io.dddrive.doc.model.base;

import io.dddrive.ddd.model.PartRepository;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPartItem;
import io.dddrive.property.model.SimpleProperty;

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
