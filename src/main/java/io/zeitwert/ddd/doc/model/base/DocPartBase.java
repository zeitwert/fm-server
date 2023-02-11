
package io.zeitwert.ddd.doc.model.base;

import java.util.List;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPart;
import io.zeitwert.ddd.doc.model.DocPartItem;
import io.zeitwert.ddd.doc.model.DocPartItemRepository;
import io.zeitwert.ddd.doc.model.DocRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;

public abstract class DocPartBase<D extends Doc> extends PartBase<D> implements DocPart<D> {

	protected DocPartBase(PartRepository<D, ?> repository, D doc, Object state) {
		super(repository, doc, state);
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartItemRepository itemRepository = DocRepository.getItemRepository();
		for (Property<?> property : this.getProperties()) {
			if (property instanceof EnumSetProperty<?>) {
				EnumSetProperty<?> enumSet = (EnumSetProperty<?>) property;
				List<DocPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
				enumSet.loadEnums(partList);
			} else if (property instanceof ReferenceSetProperty<?>) {
				ReferenceSetProperty<?> referenceSet = (ReferenceSetProperty<?>) property;
				List<DocPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
				referenceSet.loadReferences(partList);
			}
		}
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property instanceof EnumSetProperty<?>) {
			return DocRepository.getItemRepository().create(this, partListType);
		} else if (property instanceof ReferenceSetProperty<?>) {
			return DocRepository.getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

}
