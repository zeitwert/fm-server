package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjPartFields;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public abstract class ObjPartPersistenceProviderBase<O extends Obj, P extends Part<O>>
		extends PartPersistenceProviderBase<O, P>
		implements ObjPartPropertyProviderMixin, PartPersistenceProviderMixin<O, P> {

	private static final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	public ObjPartPersistenceProviderBase(Class<? extends Part<O>> intfClass, DSLContext dslContext) {
		super(intfClass, dslContext);
		this.mapFields();
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(OBJ_PART_ID_SEQ).intValue();
	}

	@Override
	public final void doInit(Part<?> part, Integer partId, O obj, Part<?> parent, CodePartListType partListType) {
		super.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(ObjPartFields.OBJ_ID, obj.getId());
	}

}
