package io.zeitwert.fm.collaboration.model.impl

import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.obj.model.base.FMObjRepositoryBase
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.collaboration.model.ObjNoteRepository
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
