package io.dddrive.doc.model

import io.dddrive.ddd.model.Aggregate
import io.dddrive.oe.model.ObjUser

interface Doc : Aggregate {

	override val meta: DocMeta

	var assignee: ObjUser?

}
