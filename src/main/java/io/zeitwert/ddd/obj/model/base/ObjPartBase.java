
package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;

public abstract class ObjPartBase<O extends Obj> extends PartBase<O> implements ObjPart<O> {

	protected ObjPartBase(PartRepository<O, ?> repository, O obj, Object state) {
		super(repository, obj, state);
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property instanceof EnumSetProperty<?>) {
			return ObjRepository.getItemRepository().create(this, partListType);
		} else if (property instanceof ReferenceSetProperty<?>) {
			return ObjRepository.getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

}
