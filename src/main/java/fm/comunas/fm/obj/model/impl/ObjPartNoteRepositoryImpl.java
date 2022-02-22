package fm.comunas.fm.obj.model.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.fm.item.model.base.ItemPartNoteRepositoryBase;
import fm.comunas.fm.obj.model.ObjPartNote;
import fm.comunas.fm.obj.model.ObjPartNoteRepository;
import fm.comunas.fm.obj.model.base.ObjPartNoteBase;

@Component("objPartNoteRepository")
public class ObjPartNoteRepositoryImpl
		extends ItemPartNoteRepositoryBase<Obj>
		implements ObjPartNoteRepository {

	private static final String PART_TYPE = "obj_part_note";
	private final String OBJ_PART_ID_SEQ = "obj_part_id_seq";

	//@formatter:off
	protected ObjPartNoteRepositoryImpl(
		AppContext appContext,
		DSLContext dslContext
	) {
		super(
			Obj.class,
			ObjPartNote.class,
			ObjPartNoteBase.class,
			PART_TYPE,
			appContext,
			dslContext
		);
	}
	//@formatter:on

	@Override
	public Integer nextPartId() {
		return this.dslContext.nextval(OBJ_PART_ID_SEQ).intValue();
	}

}
