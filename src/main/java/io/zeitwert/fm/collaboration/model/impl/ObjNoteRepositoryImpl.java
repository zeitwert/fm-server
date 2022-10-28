
package io.zeitwert.fm.collaboration.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;

@Component("objNoteRepository")
public class ObjNoteRepositoryImpl extends ObjRepositoryBase<ObjNote, ObjNoteVRecord>
		implements ObjNoteRepository {

	private static final String AGGREGATE_TYPE = "obj_note";

	//@formatter:off
	protected ObjNoteRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final ObjPartTransitionRepository transitionRepository,
		final ObjPartItemRepository itemRepository
	) {
		super(
			ObjNoteRepository.class,
			ObjNote.class,
			ObjNoteBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			itemRepository
		);
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	public ObjNote doCreate(RequestContext requestCtx) {
		return this.doCreate(requestCtx, this.getDSLContext().newRecord(Tables.OBJ_NOTE));
	}

	@Override
	public ObjNote doLoad(RequestContext requestCtx, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjNoteRecord noteRecord = this.getDSLContext().fetchOne(Tables.OBJ_NOTE,
				Tables.OBJ_NOTE.OBJ_ID.eq(objId));
		if (noteRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(requestCtx, objId, noteRecord);
	}

	@Override
	public List<ObjNoteVRecord> doFind(RequestContext requestCtx, QuerySpec querySpec) {
		List<ObjNoteVRecord> noteList = this.doFind(requestCtx, Tables.OBJ_NOTE_V, Tables.OBJ_NOTE_V.ID, querySpec);
		Integer sessionUserId = requestCtx.getUser().getId();
		noteList.removeIf(note -> note.getIsPrivate() && note.getCreatedByUserId() != sessionUserId);
		noteList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
		return noteList;
	}

}
