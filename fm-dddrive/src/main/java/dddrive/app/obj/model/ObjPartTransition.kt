package dddrive.app.obj.model

import java.time.OffsetDateTime

interface ObjPartTransition : ObjPart<Obj> {

	val userId: Any

	// val user: ObjUser

	val timestamp: OffsetDateTime

	fun init(
		userId: Any,
		timestamp: OffsetDateTime,
	)

}
