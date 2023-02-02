package io.zeitwert.ddd.obj.model.impl;

import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.base.ObjPartItemBase;
import io.zeitwert.ddd.obj.model.base.ObjPartRepositoryBase;

@Component("objPartItemRepository")
public class ObjPartItemRepositoryImpl extends ObjPartRepositoryBase<Obj, ObjPartItem>
		implements ObjPartItemRepository {

	private static final String PART_TYPE = "obj_part_item";

	protected ObjPartItemRepositoryImpl(AppContext appContext) {
		super(Obj.class, ObjPartItem.class, ObjPartItemBase.class, PART_TYPE, appContext);
	}

	@Override
	public boolean hasPartId() {
		return false;
	}

}
