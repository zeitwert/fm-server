package io.dddrive.dddrive.obj.persist.mem.pto

import io.dddrive.dddrive.ddd.persist.mem.pto.PartPto
import java.time.OffsetDateTime

open class ObjPartTransitionPto(
	var userId: Any? = null,
	var timestamp: OffsetDateTime? = null,
	id: Int? = null, // from PartPto
) : PartPto(id)
