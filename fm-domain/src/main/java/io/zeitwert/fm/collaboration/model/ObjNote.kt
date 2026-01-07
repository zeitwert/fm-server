package io.zeitwert.fm.collaboration.model

import dddrive.app.ddd.model.Aggregate
import dddrive.app.obj.model.Obj
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType

interface ObjNote : Obj {

	var relatedToId: Any?

	val relatedTo: Aggregate?

	var noteType: CodeNoteType?

	var subject: String?

	var content: String?

	var isPrivate: Boolean?

}
