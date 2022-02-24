package io.zeitwert.fm.obj.model.impl;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.fm.item.model.base.ItemPartNoteRepositoryBase;
import io.zeitwert.fm.obj.model.ObjPartNote;
import io.zeitwert.fm.obj.model.ObjPartNoteRepository;
import io.zeitwert.fm.obj.model.base.ObjPartNoteBase;

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
