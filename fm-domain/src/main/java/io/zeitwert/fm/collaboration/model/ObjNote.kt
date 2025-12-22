package io.zeitwert.fm.collaboration.model

import io.dddrive.ddd.model.Aggregate
import io.dddrive.obj.model.Obj
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType

interface ObjNote : Obj {

	var relatedToId: Any?

	val relatedTo: Aggregate?

	var noteType: CodeNoteType?

	var subject: String?

	var content: String?

	var isPrivate: Boolean?

}
