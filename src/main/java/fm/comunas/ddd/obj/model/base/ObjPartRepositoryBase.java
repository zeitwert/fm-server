package fm.comunas.ddd.obj.model.base;

import org.jooq.DSLContext;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.obj.model.ObjPart;
import fm.comunas.ddd.obj.model.ObjPartRepository;
import fm.comunas.ddd.part.model.base.PartRepositoryBase;

public abstract class ObjPartRepositoryBase<O extends Obj, P extends ObjPart<O>> extends PartRepositoryBase<O, P>
		implements ObjPartRepository<O, P> {

	private final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	//@formatter:off
	protected ObjPartRepositoryBase(
		final Class<? extends O> aggregateIntfClass,
		final Class<? extends ObjPart<O>> intfClass,
		final Class<? extends ObjPart<O>> baseClass,
		final String partTypeId,
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
	}
	//@formatter:on

	@Override
	public Integer nextPartId() {
		return this.dslContext.nextval(OBJ_PART_ID_SEQ).intValue();
	}

}
