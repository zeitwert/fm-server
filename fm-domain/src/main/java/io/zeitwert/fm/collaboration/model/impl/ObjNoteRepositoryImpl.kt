package io.zeitwert.fm.collaboration.model.impl

import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.collaboration.model.base.ObjNoteBase
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component

@Component("objNoteRepository")
class ObjNoteRepositoryImpl : FMObjRepositoryBase<ObjNote>(
	ObjNoteRepository::class.java,
	ObjNote::class.java,
	ObjNoteBase::class.java,
	AGGREGATE_TYPE_ID
), ObjNoteRepository {

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_note"
	}
}

