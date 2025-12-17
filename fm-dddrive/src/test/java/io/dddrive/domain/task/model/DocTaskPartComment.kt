package io.dddrive.domain.task.model

import io.dddrive.core.doc.model.DocPart
import java.time.OffsetDateTime

interface DocTaskPartComment : DocPart<DocTask> {
	var text: String?
	val createdAt: OffsetDateTime?
}
