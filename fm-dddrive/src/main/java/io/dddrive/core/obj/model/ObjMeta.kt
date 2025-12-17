package io.dddrive.core.obj.model

import io.dddrive.core.ddd.model.AggregateMeta
import io.dddrive.core.oe.model.ObjUser
import java.time.OffsetDateTime

interface ObjMeta : AggregateMeta {

	val objTypeId: String

	val closedAt: OffsetDateTime?

	val closedByUser: ObjUser?

	val transitionList: List<ObjPartTransition>

}
