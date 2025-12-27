package dddrive.app.obj.model

import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

interface ObjPartTransition : ObjPart<Obj> {

	val user: ObjUser

	val timestamp: OffsetDateTime

	fun init(
		userId: Any,
		timestamp: OffsetDateTime,
	)

}
