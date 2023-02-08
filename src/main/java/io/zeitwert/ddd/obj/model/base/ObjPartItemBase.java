package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.SimpleProperty;

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
