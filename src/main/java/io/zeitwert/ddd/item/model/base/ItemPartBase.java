
package io.zeitwert.ddd.item.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.model.ItemPart;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.part.model.enums.CodePartListType;

import org.jooq.UpdatableRecord;

public abstract class ItemPartBase<A extends Aggregate> extends PartBase<A> implements ItemPart<A> {

	protected ItemPartBase(PartRepository<A, ?> repository, A obj, UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
	}

	@Override
	public final void doInit(Integer partId, A aggregate, Part<?> parent, CodePartListType partListType) {
		super.doInit(partId, aggregate, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		if (partId != null) {
			dbRecord.setValue(ItemFields.ID, partId);
		}
		dbRecord.setValue(ItemFields.ITEM_ID, aggregate.getId());
		dbRecord.setValue(ItemFields.PARENT_PART_ID, parent != null ? parent.getId() : null);
		dbRecord.setValue(ItemFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
