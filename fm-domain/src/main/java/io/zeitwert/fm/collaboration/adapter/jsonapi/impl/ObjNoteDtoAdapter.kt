package io.zeitwert.fm.collaboration.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.EnumeratedDto
import io.zeitwert.app.api.jsonapi.dto.DtoUtils
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.collaboration.adapter.jsonapi.dto.ObjNoteDto
import io.zeitwert.fm.collaboration.model.ObjNote
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
		config.field(
			"relatedTo",
			{ note -> EnumeratedDto.of((note as ObjNote).relatedTo) },
			{ dtoValue, note ->
				(note as ObjNote).relatedToId = DtoUtils.idFromString(enumId(dtoValue))
			},
		)
	}

}
