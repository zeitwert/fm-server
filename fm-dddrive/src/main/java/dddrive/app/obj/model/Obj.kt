package dddrive.app.obj.model

import dddrive.ddd.core.model.Aggregate
import java.time.OffsetDateTime

interface Obj : dddrive.ddd.core.model.Aggregate {

	override val meta: ObjMeta

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	fun delete(
		userId: Any?,
		timestamp: OffsetDateTime?,
	)

}
