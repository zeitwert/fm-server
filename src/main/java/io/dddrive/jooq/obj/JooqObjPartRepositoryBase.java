package io.dddrive.jooq.obj;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.jooq.ddd.JooqPartRepositoryBase;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPart;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.fm.obj.model.base.ObjPartFields;

public abstract class JooqObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>>
		extends JooqPartRepositoryBase<O, P>
		implements ObjPartPropertyProviderMixin {

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
	public final void doInit(Part<?> part, Integer partId, O obj, Part<?> parent, CodePartListType partListType) {
		this.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(ObjPartFields.OBJ_ID, obj.getId());
	}

}
