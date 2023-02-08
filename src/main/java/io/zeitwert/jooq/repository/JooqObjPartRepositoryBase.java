package io.zeitwert.jooq.repository;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.jooq.property.ObjPartFields;
import io.zeitwert.jooq.property.ObjPartPropertyProviderMixin;

public abstract class JooqObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>>
		extends JooqPartRepositoryBase<O, P>
		implements ObjPartPropertyProviderMixin {

	private static final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	protected JooqObjPartRepositoryBase(
			Class<? extends O> aggregateIntfClass,
			Class<? extends ObjPart<O>> intfClass,
			Class<? extends ObjPart<O>> baseClass,
			String partTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
		this.mapProperties();
	}

	@Override
	public Integer nextPartId() {
		return this.dslContext().nextval(OBJ_PART_ID_SEQ).intValue();
	}

	@Override
	public final void doInit(Part<?> part, Integer partId, O obj, Part<?> parent, CodePartListType partListType) {
		this.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(ObjPartFields.OBJ_ID, obj.getId());
	}

}
