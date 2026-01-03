package io.zeitwert.fm.collaboration.model.impl

import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
import io.zeitwert.fm.obj.model.base.FMObjRepositoryBase
import org.springframework.stereotype.Component

@Component("objNoteRepository")
class ObjNoteRepositoryImpl(
	override val sessionContext: SessionContext,
) : FMObjRepositoryBase<ObjNote>(
		ObjNote::class.java,
		AGGREGATE_TYPE_ID,
	),
	ObjNoteRepository {

	override fun createAggregate(isNew: Boolean): ObjNote = ObjNoteImpl(this, isNew)

	companion object {

		private const val AGGREGATE_TYPE_ID = "obj_note"
	}

}
