package io.zeitwert.fm.contact.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.contact.api.jsonapi.dto.ObjContactDto
import io.zeitwert.fm.contact.model.ObjContact
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
