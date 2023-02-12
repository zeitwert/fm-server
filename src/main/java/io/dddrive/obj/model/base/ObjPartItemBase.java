package io.dddrive.obj.model.base;

import io.dddrive.ddd.model.PartRepository;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPartItem;
import io.dddrive.property.model.SimpleProperty;

public abstract class ObjPartItemBase extends ObjPartBase<Obj> implements ObjPartItem {

	protected final SimpleProperty<String> itemId = this.addSimpleProperty("itemId", String.class);

	public ObjPartItemBase(PartRepository<Obj, ?> repository, Obj obj, Object state) {
		super(repository, obj, state);
	}

	@Override
	public String toString() {
		return "ObjPartItem["
				+ this.getAggregate().getId() + "|"
				+ this.getParentPartId() + "|"
				+ this.getPartListTypeId() + "]: "
				+ this.getItemId();
	}

}
