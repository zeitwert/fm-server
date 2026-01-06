package io.zeitwert.fm.contact.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objContactDtoAdapter")
class ObjContactDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjContact, ObjContactDto>(
	ObjContact::class.java,
	"contact",
	ObjContactDto::class.java,
	directory,
	{ ObjContactDto() },
)
