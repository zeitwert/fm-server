package io.dddrive.obj.model

import io.dddrive.ddd.model.AggregateMeta
import io.dddrive.oe.model.ObjUser
import java.time.OffsetDateTime

interface ObjMeta : AggregateMeta {

	val objTypeId: String

	val closedAt: OffsetDateTime?

	val closedByUser: ObjUser?

	val transitionList: List<ObjPartTransition>

}
