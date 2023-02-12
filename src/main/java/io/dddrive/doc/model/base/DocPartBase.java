
package io.dddrive.doc.model.base;

import java.util.List;

import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.ddd.model.base.PartBase;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPart;
import io.dddrive.doc.model.DocPartItem;
import io.dddrive.doc.model.DocPartItemRepository;
import io.dddrive.doc.model.DocRepository;
import io.dddrive.property.model.EnumSetProperty;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.ReferenceSetProperty;

public abstract class DocPartBase<D extends Doc> extends PartBase<D> implements DocPart<D> {

	protected DocPartBase(PartRepository<D, ?> repository, D doc, Object state) {
		super(repository, doc, state);
	}

	@SuppressWarnings("unchecked")
	protected DocRepository<D, ?> getDocRepository() {
		return (DocRepository<D, ?>) this.getAggregate().getMeta().getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		DocPartItemRepository itemRepository = this.getDocRepository().getItemRepository();
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
			return this.getDocRepository().getItemRepository().create(this, partListType);
		} else if (property instanceof ReferenceSetProperty<?>) {
			return this.getDocRepository().getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

}
