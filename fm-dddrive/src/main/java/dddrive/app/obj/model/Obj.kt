package dddrive.app.obj.model

import java.time.OffsetDateTime

interface Obj : dddrive.ddd.core.model.Aggregate {

	override val meta: ObjMeta

	val tenantId: Any

	// val tenant: ObjTenant

	var ownerId: Any?

	// var owner: ObjUser?

	val caption: String

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	fun delete(
		userId: Any?,
		timestamp: OffsetDateTime?,
	)

}
