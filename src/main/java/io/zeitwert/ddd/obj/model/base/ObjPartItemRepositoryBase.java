package io.zeitwert.ddd.obj.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartItem;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;

public abstract class ObjPartItemRepositoryBase extends ObjPartRepositoryBase<Obj, ObjPartItem>
		implements ObjPartItemRepository {

	private static final String PART_TYPE = "obj_part_item";

	protected ObjPartItemRepositoryBase(final AppContext appContext, final DSLContext dslContext) {
		super(
				Obj.class,
				ObjPartItem.class,
				ObjPartItemBase.class,
				PART_TYPE,
				appContext,
				dslContext);
	}

	@Override
	public boolean hasPartId() {
		return false;
	}

}
