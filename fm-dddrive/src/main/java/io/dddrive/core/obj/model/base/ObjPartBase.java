package io.dddrive.core.obj.model.base;

import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.ddd.model.base.PartBase;
import io.dddrive.core.obj.model.Obj;
import io.dddrive.core.obj.model.ObjPart;
import io.dddrive.core.property.model.Property;

public abstract class ObjPartBase<O extends Obj> extends PartBase<O> implements ObjPart<O> {

	protected ObjPartBase(O obj, PartRepository<O, ? extends Part<O>> repository, Property<?> property, int id) {
		super(obj, repository, property, id);
	}

//	@SuppressWarnings("unchecked")
//	protected ObjRepository<O> getObjRepository() {
//		return (ObjRepository<O>) this.getAggregate().getMeta().getRepository();
//	}

//	@Override
//	public void doAssignParts() {
//		super.doAssignParts();
//		ObjPartItemRepository itemRepository = this.getObjRepository().getItemRepository();
//		for (Property<?> property : this.getProperties()) {
//			if (property instanceof EnumSetProperty<?> enumSet) {
//				List<ObjPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
//				enumSet.loadEnums(partList);
//			} else if (property instanceof ReferenceSetProperty<?> referenceSet) {
//				List<ObjPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
//				referenceSet.loadReferences(partList);
//			}
//		}
//	}

//	@Override
//	public Part<?> doAddPart(Property<?> property) {
//		if (property instanceof EnumSetProperty<?>) {
//			return this.getObjRepository().getItemRepository().create(this);
//		} else if (property instanceof ReferenceSetProperty<?>) {
//			return this.getObjRepository().getItemRepository().create(this);
//		}
//		return super.doAddPart(property);
//	}

}
