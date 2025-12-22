package io.dddrive.obj.model

import io.dddrive.ddd.model.Aggregate
import java.time.OffsetDateTime

interface Obj : Aggregate {

	override val meta: ObjMeta

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	fun delete(
		userId: Any?,
		timestamp: OffsetDateTime?,
	)

}
