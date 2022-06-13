
package io.zeitwert.fm.collaboration.model.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.zeitwert.ddd.util.Check.requireThis;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteVRecord;

import javax.annotation.PostConstruct;

import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.obj.model.ObjPartTransitionRepository;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.ddd.session.model.SessionInfo;

@Component("objNoteRepository")
public class ObjNoteRepositoryImpl extends ObjRepositoryBase<ObjNote, ObjNoteVRecord>
		implements ObjNoteRepository {

	private static final String ITEM_TYPE = "obj_note";

	@Autowired
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
			ITEM_TYPE,
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
	public ObjNote doCreate(SessionInfo sessionInfo) {
		return this.doCreate(sessionInfo, this.getDSLContext().newRecord(Tables.OBJ_NOTE));
	}

	@Override
	public ObjNote doLoad(SessionInfo sessionInfo, Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjNoteRecord noteRecord = this.getDSLContext().fetchOne(Tables.OBJ_NOTE,
				Tables.OBJ_NOTE.OBJ_ID.eq(objId));
		if (noteRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(sessionInfo, objId, noteRecord);
	}

	@Override
	public List<ObjNoteVRecord> doFind(SessionInfo sessionInfo, QuerySpec querySpec) { // TODO move to doFind below
		List<ObjNoteVRecord> noteList = this.doFind(sessionInfo, Tables.OBJ_NOTE_V, Tables.OBJ_NOTE_V.ID, querySpec);
		noteList.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
		noteList = noteList.stream()
				.filter(n -> !n.getIsPrivate() || n.getCreatedByUserId() == sessionInfo.getUser().getId()).toList();
		return noteList;
	}

}
