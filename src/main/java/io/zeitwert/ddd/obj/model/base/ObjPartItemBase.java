package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.db.tables.records.ObjPartItemRecord;
import io.zeitwert.ddd.property.model.SimpleProperty;

import org.jooq.UpdatableRecord;

public abstract class ObjPartItemBase extends ObjPartBase<Obj> implements ObjPartItem {

	protected final SimpleProperty<String> itemId;

	public ObjPartItemBase(Obj obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
		this.itemId = this.addSimpleProperty(dbRecord, ObjPartItemFields.ITEM_ID);
	}

	@Override
	// since we have a combined primary key, parentPartId must be "null"
	public void afterCreate() {
		if (this.getParentPartId() == null) {
			((ObjPartItemRecord) this.getDbRecord()).setParentPartId(0);
		}
	}

	@Override
	public String toString() {
		return "ObjPartItem[" + this.getAggregate().getId() + "|" + this.getParentPartId() + "|" + this.getPartListTypeId()
				+ "]: " + this.getItemId();
	}

}
