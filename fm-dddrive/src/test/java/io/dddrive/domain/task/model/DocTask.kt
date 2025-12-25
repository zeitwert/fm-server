package io.dddrive.domain.task.model

import io.dddrive.doc.model.Doc
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import io.dddrive.property.model.PartListProperty
import java.time.OffsetDateTime

interface DocTask : Doc {

	var subject: String?
	var content: String?
	var isPrivate: Boolean?
	var priority: CodeTaskPriority?
	var dueAt: OffsetDateTime?
	var remindAt: OffsetDateTime?

	val commentList: PartListProperty<DocTaskPartComment>

}
