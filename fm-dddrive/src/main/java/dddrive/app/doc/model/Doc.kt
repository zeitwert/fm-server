package dddrive.app.doc.model

import dddrive.app.ddd.model.Aggregate

interface Doc : Aggregate {

	override val meta: DocMeta

	var assigneeId: Any?

	// var assignee: ObjUser?

}
