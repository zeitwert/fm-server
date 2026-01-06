package io.zeitwert.fm.collaboration.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.fm.collaboration.adapter.api.jsonapi.dto.ObjNoteDto
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objNoteDtoAdapter")
class ObjNoteDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjNote, ObjNoteDto>(
	ObjNote::class.java,
	"note",
	ObjNoteDto::class.java,
	directory,
	{ ObjNoteDto() },
) {

	init {
		config.field("relatedTo")
	}

}
