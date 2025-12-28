package dddrive.domain.task.model

import dddrive.app.doc.model.Doc
import dddrive.ddd.property.model.PartListProperty
import dddrive.domain.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

interface DocTask : Doc {

	var subject: String?
	var content: String?
	var isPrivate: Boolean?
	var priority: CodeTaskPriority?
	var dueAt: OffsetDateTime?
	var remindAt: OffsetDateTime?

	val commentList: PartListProperty<DocTask, DocTaskPartComment>

}
