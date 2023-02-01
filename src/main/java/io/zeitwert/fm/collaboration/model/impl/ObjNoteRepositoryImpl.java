
package io.zeitwert.fm.collaboration.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;

@Component("objNoteRepository")
public class ObjNoteRepositoryImpl extends ObjRepositoryBase<ObjNote, ObjNoteVRecord>
		implements ObjNoteRepository {

	private static final String AGGREGATE_TYPE = "obj_note";

	private final RequestContext requestCtx;

	protected ObjNoteRepositoryImpl(final AppContext appContext, final DSLContext dslContext,
			final RequestContext requestCtx) {
		super(ObjNoteRepository.class, ObjNote.class, ObjNoteBase.class, AGGREGATE_TYPE, appContext, dslContext);
		this.requestCtx = requestCtx;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	public List<ObjNoteVRecord> doFind(QuerySpec querySpec) {
		List<ObjNoteVRecord> notes = this.doFind(Tables.OBJ_NOTE_V, Tables.OBJ_NOTE_V.ID, querySpec);
		Integer userId = this.requestCtx.getUser().getId();
		notes.removeIf(n -> !this.isVisible(n, userId));
		notes.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
		return notes;
	}

	private boolean isVisible(ObjNoteVRecord note, Integer userId) {
		return !note.getIsPrivate() || userId.equals(note.getCreatedByUserId()) || userId.equals(note.getOwnerId());
	}

}
