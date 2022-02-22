
package fm.comunas.ddd.item.model.base;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.item.model.ItemPart;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.part.model.base.PartBase;
import fm.comunas.ddd.property.model.enums.CodePartListType;

import org.jooq.UpdatableRecord;

public abstract class ItemPartBase<A extends Aggregate> extends PartBase<A> implements ItemPart<A> {

	protected ItemPartBase(A obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
	}

	@Override
	public final void doInit(Integer partId, A obj, Part<?> parent, CodePartListType partListType) {
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		if (partId != null) {
			dbRecord.setValue(ItemPartFields.ID, partId);
		}
		dbRecord.setValue(ItemPartFields.ITEM_ID, obj.getId());
		dbRecord.setValue(ItemPartFields.PARENT_PART_ID, parent != null ? parent.getId() : null);
		dbRecord.setValue(ItemPartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
