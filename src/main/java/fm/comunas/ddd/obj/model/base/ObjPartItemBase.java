package fm.comunas.ddd.obj.model.base;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPartItem;
import fm.comunas.ddd.obj.model.db.tables.records.ObjPartItemRecord;
import fm.comunas.ddd.property.model.SimpleProperty;

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
