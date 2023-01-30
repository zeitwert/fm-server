package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;

public abstract class ObjPartTransitionRepositoryBase extends ObjPartRepositoryBase<Obj, ObjPartTransition>
		implements ObjPartTransitionRepository {

	private static final String PART_TYPE = "obj_part_transition";

	protected ObjPartTransitionRepositoryBase(final AppContext appContext, final DSLContext dslContext) {
		super(
				Obj.class,
				ObjPartTransition.class,
				ObjPartTransitionBase.class,
				PART_TYPE,
				appContext,
				dslContext);
	}

}
