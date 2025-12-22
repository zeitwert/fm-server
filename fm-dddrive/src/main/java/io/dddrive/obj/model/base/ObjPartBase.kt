package io.dddrive.obj.model.base

import io.dddrive.ddd.model.Part
import io.dddrive.ddd.model.PartRepository
import io.dddrive.ddd.model.base.PartBase
import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjPart
import io.dddrive.property.model.Property

abstract class ObjPartBase<O : Obj>(
	obj: O,
	repository: PartRepository<O, out Part<O>>,
	property: Property<*>,
	id: Int,
) : PartBase<O>(obj, repository, property, id),
	ObjPart<O>

// 	@SuppressWarnings("unchecked")
// 	protected ObjRepository<O> getObjRepository() {
// 		return (ObjRepository<O>) this.getAggregate().getMeta().getRepository();
// 	}
// 	@Override
// 	public void doAssignParts() {
// 		super.doAssignParts();
// 		ObjPartItemRepository itemRepository = this.getObjRepository().getItemRepository();
// 		for (Property<?> property : this.getProperties()) {
// 			if (property instanceof EnumSetProperty<?> enumSet) {
// 				List<ObjPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
// 				enumSet.loadEnums(partList);
// 			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
// 				List<ObjPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
// 				referenceSet.loadReferences(partList);
// 			}
// 		}
// 	}
// 	@Override
// 	public Part<?> doAddPart(Property<?> property) {
// 		if (property instanceof EnumSetProperty<?>) {
// 			return this.getObjRepository().getItemRepository().create(this);
// 		} else if (property instanceof ReferenceSetProperty<?>) {
// 			return this.getObjRepository().getItemRepository().create(this);
// 		}
// 		return super.doAddPart(property);
// 	}
