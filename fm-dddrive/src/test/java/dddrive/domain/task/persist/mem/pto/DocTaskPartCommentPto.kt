package dddrive.domain.task.persist.mem.pto

import dddrive.domain.ddd.persist.mem.pto.PartPto
import java.time.OffsetDateTime

open class DocTaskPartCommentPto(
	var text: String? = null,
	var createdAt: OffsetDateTime? = null,
	id: Int? = null,
) : PartPto(id)
