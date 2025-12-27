package dddrive.domain.doc.persist.mem.pto

import dddrive.domain.ddd.persist.mem.pto.PartPto
import java.time.OffsetDateTime

open class DocPartTransitionPto(
	var userId: Any? = null,
	var timestamp: OffsetDateTime? = null,
	var oldCaseStageId: String? = null,
	var newCaseStageId: String? = null,
	id: Int? = null, // from PartPto
) : PartPto(id)
