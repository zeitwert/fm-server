package dddrive.domain.task.model

import dddrive.app.doc.model.DocPart
import java.time.OffsetDateTime

interface DocTaskPartComment : DocPart<DocTask> {

	var text: String?
	val createdAt: OffsetDateTime?
}
