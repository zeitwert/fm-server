
package io.zeitwert.fm.collaboration.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.util.List;

import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.jooq.ddd.AggregateState;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase;

@Component("objNoteRepository")
public class ObjNoteRepositoryImpl extends FMObjRepositoryBase<ObjNote, ObjNoteVRecord> implements ObjNoteRepository {

	private static final String AGGREGATE_TYPE = "obj_note";

	protected ObjNoteRepositoryImpl() {
		super(ObjNoteRepository.class, ObjNote.class, ObjNoteBase.class, AGGREGATE_TYPE);
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("relatedToId", AggregateState.EXTN, "related_to_id", Integer.class);
		this.mapField("noteType", AggregateState.EXTN, "note_type_id", String.class);
		this.mapField("subject", AggregateState.EXTN, "subject", String.class);
		this.mapField("content", AggregateState.EXTN, "content", String.class);
		this.mapField("isPrivate", AggregateState.EXTN, "is_private", Boolean.class);
	}

	@Override
	public boolean hasAccount() {
		return false;
	}

	@Override
	public ObjNote doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_NOTE));
	}

	@Override
	public ObjNote doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjNoteRecord noteRecord = this.dslContext().fetchOne(Tables.OBJ_NOTE,
				Tables.OBJ_NOTE.OBJ_ID.eq(objId));
		if (noteRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, noteRecord);
	}

	@Override
	public List<ObjNoteVRecord> doFind(QuerySpec querySpec) {
		List<ObjNoteVRecord> notes = this.doFind(Tables.OBJ_NOTE_V, Tables.OBJ_NOTE_V.ID, querySpec);
		Integer userId = this.getAppContext().getRequestContext().getUser().getId();
		notes.removeIf(n -> !this.isVisible(n, userId));
		notes.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
		return notes;
	}

	private boolean isVisible(ObjNoteVRecord note, Integer userId) {
		return !note.getIsPrivate() || userId.equals(note.getCreatedByUserId()) || userId.equals(note.getOwnerId());
	}

}
