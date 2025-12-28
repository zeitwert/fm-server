package dddrive.app.obj.model

import dddrive.app.ddd.model.AggregateMeta
import java.time.OffsetDateTime

interface ObjMeta : AggregateMeta {

	val objTypeId: String

	// val closedByUser: ObjUser?
	val closedByUserId: Any?
	val closedAt: OffsetDateTime?

	val transitionList: List<ObjPartTransition>

}
