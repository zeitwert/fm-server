package dddrive.app.doc.model

import dddrive.ddd.core.model.Aggregate
import io.dddrive.oe.model.ObjUser

interface Doc : dddrive.ddd.core.model.Aggregate {

	override val meta: DocMeta

	var assignee: ObjUser?

}
