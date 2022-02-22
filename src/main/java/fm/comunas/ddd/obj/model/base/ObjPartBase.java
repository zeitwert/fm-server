
package fm.comunas.ddd.obj.model.base;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPart;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.part.model.base.PartBase;
import fm.comunas.ddd.property.model.enums.CodePartListType;

import org.jooq.UpdatableRecord;

public abstract class ObjPartBase<O extends Obj> extends PartBase<O> implements ObjPart<O> {

	protected ObjPartBase(O obj, UpdatableRecord<?> dbRecord) {
		super(obj, dbRecord);
	}

	@Override
	public final void doInit(Integer partId, O obj, Part<?> parent, CodePartListType partListType) {
		UpdatableRecord<?> dbRecord = this.getDbRecord();
		if (partId != null) {
			dbRecord.setValue(ObjPartFields.ID, partId);
		}
		dbRecord.setValue(ObjPartFields.OBJ_ID, obj.getId());
		dbRecord.setValue(ObjPartFields.PARENT_PART_ID, parent != null ? parent.getId() : null);
		dbRecord.setValue(ObjPartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
