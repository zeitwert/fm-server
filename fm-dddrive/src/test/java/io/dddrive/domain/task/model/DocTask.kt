package io.dddrive.domain.task.model

import io.dddrive.core.doc.model.Doc
import io.dddrive.domain.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

interface DocTask : Doc {

	var subject: String?
	var content: String?
	var private: Boolean?
	var priority: CodeTaskPriority?
	var dueAt: OffsetDateTime?
	var remindAt: OffsetDateTime?

	val commentList: List<DocTaskPartComment>

	fun addComment(): DocTaskPartComment

	fun removeComment(commentId: Int)
}
