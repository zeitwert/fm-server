package io.zeitwert.ddd.obj.model.impl;

import java.util.List;

import org.jooq.DSLContext;
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

	protected ObjPartItemRepositoryImpl(final AppContext appContext, final DSLContext dslContext) {
		super(Obj.class, ObjPartItem.class, ObjPartItemBase.class, PART_TYPE, appContext, dslContext);
	}

	@Override
	public boolean hasPartId() {
		return false;
	}

	@Override
	public ObjPartItem doCreate(Obj obj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ObjPartItem> doLoad(Obj obj) {
		throw new UnsupportedOperationException();
	}

}
