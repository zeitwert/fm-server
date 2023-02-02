package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.jooq.base.PartPersistenceProviderBase;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public abstract class ObjPartPersistenceProviderBase<O extends Obj, P extends Part<O>>
		extends PartPersistenceProviderBase<O, P> {

	private static final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	public ObjPartPersistenceProviderBase(
			final Class<? extends O> objIntfClass,
			Class<? extends PartRepository<O, P>> repoIntfClass,
			Class<? extends Part<O>> baseClass,
			DSLContext dslContext) {
		super(objIntfClass, repoIntfClass, baseClass, dslContext);
		this.mapField("objId", BASE, "obj_id", Integer.class);
	}

	@Override
	public Class<?> getEntityClass() { // TODO: remove here
		return null;
	}

	@Override
	public boolean isReal() {
		return false;
	}

	@Override
	public Integer nextPartId() {
		return this.getDSLContext().nextval(OBJ_PART_ID_SEQ).intValue();
	}

	@Override
	public final void doInit(Part<?> part, Integer partId, O obj, Part<?> parent, CodePartListType partListType) {
		super.doInit(part, partId, parent, partListType);
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		dbRecord.setValue(ObjPartFields.OBJ_ID, obj.getId());
	}

}
