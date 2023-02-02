package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPart;
import io.zeitwert.ddd.obj.model.ObjPartRepository;
import io.zeitwert.ddd.part.model.base.PartRepositoryBase;
import io.zeitwert.ddd.persistence.PartPersistenceProvider;

public abstract class ObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>> extends PartRepositoryBase<O, P>
		implements ObjPartRepository<O, P> {

	private static final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	protected ObjPartRepositoryBase(
			final Class<? extends O> aggregateIntfClass,
			final Class<? extends ObjPart<O>> intfClass,
			final Class<? extends ObjPart<O>> baseClass,
			final String partTypeId,
			final AppContext appContext,
			final DSLContext dslContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
	}

	@Override
	public Integer nextPartId() {
		PartPersistenceProvider<O, P> persistenceProvider = this.getPersistenceProvider();
		if (persistenceProvider != null && persistenceProvider.isReal()) {
			return persistenceProvider.nextPartId();
		} else {
			return this.getDSLContext().nextval(OBJ_PART_ID_SEQ).intValue();
		}
	}

}
