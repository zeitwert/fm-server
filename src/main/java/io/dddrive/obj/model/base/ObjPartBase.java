
package io.dddrive.obj.model.base;

import java.util.List;

import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.ddd.model.base.PartBase;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPart;
import io.dddrive.obj.model.ObjPartItem;
import io.dddrive.obj.model.ObjPartItemRepository;
import io.dddrive.obj.model.ObjRepository;
import io.dddrive.property.model.EnumSetProperty;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.ReferenceSetProperty;

public abstract class ObjPartBase<O extends Obj> extends PartBase<O> implements ObjPart<O> {

	protected ObjPartBase(PartRepository<O, ?> repository, O obj, Object state) {
		super(repository, obj, state);
	}

	@SuppressWarnings("unchecked")
	protected ObjRepository<O, ?> getObjRepository() {
		return (ObjRepository<O, ?>) this.getAggregate().getMeta().getRepository();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjPartItemRepository itemRepository = this.getObjRepository().getItemRepository();
		for (Property<?> property : this.getProperties()) {
			if (property instanceof EnumSetProperty<?>) {
				EnumSetProperty<?> enumSet = (EnumSetProperty<?>) property;
				List<ObjPartItem> partList = itemRepository.getParts(this, enumSet.getPartListType());
				enumSet.loadEnums(partList);
			} else if (property instanceof ReferenceSetProperty<?>) {
				ReferenceSetProperty<?> referenceSet = (ReferenceSetProperty<?>) property;
				List<ObjPartItem> partList = itemRepository.getParts(this, referenceSet.getPartListType());
				referenceSet.loadReferences(partList);
			}
		}
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property instanceof EnumSetProperty<?>) {
			return this.getObjRepository().getItemRepository().create(this, partListType);
		} else if (property instanceof ReferenceSetProperty<?>) {
			return this.getObjRepository().getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

}
