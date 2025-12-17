package io.dddrive.core.doc.model

import io.dddrive.core.ddd.model.Aggregate
import io.dddrive.core.oe.model.ObjUser

interface Doc : Aggregate {

	override val meta: DocMeta

	var assignee: ObjUser?

}
