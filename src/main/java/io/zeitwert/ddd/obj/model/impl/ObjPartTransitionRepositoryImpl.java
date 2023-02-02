package io.zeitwert.ddd.obj.model.impl;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;
import io.zeitwert.ddd.obj.model.base.ObjPartTransitionBase;

@Component("objPartTransitionRepository")
public class ObjPartTransitionRepositoryImpl extends ObjPartRepositoryBase<Obj, ObjPartTransition>
		implements ObjPartTransitionRepository {

	private static final String PART_TYPE = "obj_part_transition";

	protected ObjPartTransitionRepositoryImpl(AppContext appContext) {
		super(Obj.class, ObjPartTransition.class, ObjPartTransitionBase.class, PART_TYPE, appContext);
	}

}
