
package io.zeitwert.fm.collaboration.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.obj.model.db.tables.records.ObjRecord;

@Component("objNoteRepository")
public class ObjNoteRepositoryImpl extends ObjRepositoryBase<ObjNote, ObjNoteVRecord>
		implements ObjNoteRepository {

	private static final String AGGREGATE_TYPE = "obj_note";

	private final RequestContext requestCtx;

	protected ObjNoteRepositoryImpl(
			final AppContext appContext,
			final DSLContext dslContext,
			final RequestContext requestCtx) {
		super(
				ObjNoteRepository.class,
				ObjNote.class,
				ObjNoteBase.class,
				AGGREGATE_TYPE,
				appContext,
				dslContext);
		this.requestCtx = requestCtx;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	public ObjNote doCreate() {
		UpdatableRecord<?> objRecord = this.getDSLContext().newRecord(io.zeitwert.fm.obj.model.db.Tables.OBJ);
		UpdatableRecord<?> extnRecord = this.getDSLContext().newRecord(Tables.OBJ_NOTE);
		return this.newAggregate(objRecord, extnRecord);
	}

	@Override
	public ObjNote doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjRecord objRecord = this.getDSLContext().fetchOne(
				io.zeitwert.fm.obj.model.db.Tables.OBJ, io.zeitwert.fm.obj.model.db.Tables.OBJ.ID.eq(objId));
		if (objRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		ObjNoteRecord extnRecord = this.getDSLContext().fetchOne(Tables.OBJ_NOTE,
				Tables.OBJ_NOTE.OBJ_ID.eq(objId));
		if (extnRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.newAggregate(objRecord, extnRecord);
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
