package dddrive.app.obj.model.base

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjPart
import dddrive.ddd.core.model.Part
import dddrive.ddd.core.model.PartRepository
import dddrive.ddd.core.model.base.PartBase
import dddrive.ddd.property.model.Property

abstract class ObjPartBase<O : Obj>(
	obj: O,
	repository: dddrive.ddd.core.model.PartRepository<O, out dddrive.ddd.core.model.Part<O>>,
	property: dddrive.ddd.property.model.Property<*>,
	id: Int,
) : dddrive.ddd.core.model.base.PartBase<O>(obj, repository, property, id),
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
