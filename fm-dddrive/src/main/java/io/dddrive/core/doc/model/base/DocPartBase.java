package io.dddrive.core.doc.model.base;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.ddd.model.base.PartBase;
import io.dddrive.core.doc.model.Doc;
import io.dddrive.core.doc.model.DocPart;
import io.dddrive.core.property.model.Property;

public abstract class DocPartBase<D extends Doc> extends PartBase<D> implements DocPart<D> {

	protected DocPartBase(D doc, PartRepository<D, ? extends Part<D>> repository, Property<?> property, Integer id) {
		super(doc, repository, property, id);
	}

//	@SuppressWarnings("unchecked")
//	protected DocRepository<D> getDocRepository() {
//		return (DocRepository<D>) this.getAggregate().getMeta().getRepository();
//	}

//	@Override
//	public void doAssignParts() {
//		super.doAssignParts();
//		DocPartItemRepository itemRepository = this.getDocRepository().getItemRepository();
//		for (Property<?> property : this.getProperties()) {
//			if (property instanceof EnumSetProperty<?> enumSet) {
//				List<DocPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
//				enumSet.loadEnums(partList);
//			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
//				List<DocPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
//				referenceSet.loadReferences(partList);
//			}
//		}
//	}

//	@Override
//	public Part<?> doAddPart(Property<?> property) {
//		if (property instanceof EnumSetProperty<?>) {
//			return this.getDocRepository().getItemRepository().create(this);
//		} else if (property instanceof ReferenceSetProperty<?>) {
//			return this.getDocRepository().getItemRepository().create(this);
//		}
//		return super.doAddPart(property);
//	}

}
