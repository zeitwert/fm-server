
package io.zeitwert.fm.collaboration.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.ddd.obj.model.base.ObjPersistenceProviderBase;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase;
import io.zeitwert.fm.collaboration.model.db.Tables;
import io.zeitwert.fm.collaboration.model.db.tables.records.ObjNoteRecord;

@Configuration("notePersistenceProvider")
public class ObjNotePersistenceProvider extends ObjPersistenceProviderBase<ObjNote> {

	public ObjNotePersistenceProvider(DSLContext dslContext) {
		super(ObjNoteRepository.class, ObjNoteBase.class, dslContext);
		this.mapField("relatedToId", DbTableType.EXTN, "related_to_id", Integer.class);
		this.mapField("noteType", DbTableType.EXTN, "note_type_id", String.class);
		this.mapField("subject", DbTableType.EXTN, "subject", String.class);
		this.mapField("content", DbTableType.EXTN, "content", String.class);
		this.mapField("isPrivate", DbTableType.EXTN, "is_private", Boolean.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjNote.class;
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public ObjNote doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_NOTE));
	}

	@Override
	public ObjNote doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjNoteRecord noteRecord = this.getDSLContext().fetchOne(Tables.OBJ_NOTE,
				Tables.OBJ_NOTE.OBJ_ID.eq(objId));
		if (noteRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, noteRecord);
	}

}
