package dddrive.app.doc.model.base

import dddrive.app.doc.model.Doc
import dddrive.app.doc.model.DocPart
import dddrive.ddd.model.Part
import dddrive.ddd.model.PartRepository
import dddrive.ddd.model.base.PartBase

abstract class DocPartBase<D : Doc>(
	doc: D,
	repository: PartRepository<D, out Part<D>>,
	property: dddrive.property.model.Property<*>,
	id: Int,
) : PartBase<D>(doc, repository, property, id),
	DocPart<D>

// 	@SuppressWarnings("unchecked")
// 	protected DocRepository<D> getDocRepository() {
// 		return (DocRepository<D>) this.getAggregate().getMeta().getRepository();
// 	}
// 	@Override
// 	public void doAssignParts() {
// 		super.doAssignParts();
// 		DocPartItemRepository itemRepository = this.getDocRepository().getItemRepository();
// 		for (Property<?> property : this.getProperties()) {
// 			if (property instanceof EnumSetProperty<?> enumSet) {
// 				List<DocPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
// 				enumSet.loadEnums(partList);
// 			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
// 				List<DocPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
// 				referenceSet.loadReferences(partList);
// 			}
// 		}
// 	}
// 	@Override
// 	public Part<?> doAddPart(Property<?> property) {
// 		if (property instanceof EnumSetProperty<?>) {
// 			return this.getDocRepository().getItemRepository().create(this);
// 		} else if (property instanceof ReferenceSetProperty<?>) {
// 			return this.getDocRepository().getItemRepository().create(this);
// 		}
// 		return super.doAddPart(property);
// 	}
