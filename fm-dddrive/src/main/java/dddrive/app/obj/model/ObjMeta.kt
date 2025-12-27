package dddrive.app.obj.model

import dddrive.ddd.core.model.AggregateMeta
import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

interface ObjMeta : dddrive.ddd.core.model.AggregateMeta {

	val objTypeId: String

	val closedAt: OffsetDateTime?

	val closedByUser: ObjUser?

	val transitionList: List<ObjPartTransition>

}
