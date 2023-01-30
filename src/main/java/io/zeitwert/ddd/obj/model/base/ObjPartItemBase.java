package io.zeitwert.ddd.obj.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.SimpleProperty;

public abstract class ObjPartItemBase extends ObjPartBase<Obj> implements ObjPartItem {

	protected final SimpleProperty<String> itemId;

	public ObjPartItemBase(PartRepository<Obj, ?> repository, Obj obj, UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
		this.itemId = this.addSimpleProperty(dbRecord, ObjPartItemFields.ITEM_ID);
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
