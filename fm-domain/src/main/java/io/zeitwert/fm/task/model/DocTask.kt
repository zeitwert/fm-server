package io.zeitwert.fm.task.model

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.doc.model.Doc
import io.zeitwert.fm.account.model.ItemWithAccount
import io.zeitwert.fm.collaboration.model.ItemWithNotes
import io.zeitwert.fm.task.model.enums.CodeTaskPriority
import java.time.OffsetDateTime

interface DocTask :
	Doc,
	ItemWithAccount,
	ItemWithNotes {

	var relatedToId: Any?

	val relatedTo: Aggregate?

	var subject: String?

	var content: String?

	var isPrivate: Boolean?

	var priority: CodeTaskPriority?

	var dueAt: OffsetDateTime?

	var remindAt: OffsetDateTime?

}
