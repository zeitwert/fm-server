
package io.zeitwert.ddd.obj.model.base;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

import org.jooq.UpdatableRecord;

public abstract class ObjPartBase<O extends Obj> extends PartBase<O> implements ObjPart<O> {

	protected ObjPartBase(PartRepository<O, ?> repository, O obj, UpdatableRecord<?> dbRecord) {
		super(repository, obj, dbRecord);
	}

	@Override
	public final void doInit(Integer partId, O obj, Part<?> parent, CodePartListType partListType) {
		super.doInit(partId, obj, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		if (partId != null) {
			dbRecord.setValue(ObjPartFields.ID, partId);
		}
		dbRecord.setValue(ObjPartFields.OBJ_ID, obj.getId());
		dbRecord.setValue(ObjPartFields.PARENT_PART_ID, parent != null ? parent.getId() : null);
		dbRecord.setValue(ObjPartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
