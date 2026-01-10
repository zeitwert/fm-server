package io.zeitwert.fm.contact.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.model.SessionContext
import io.zeitwert.dddrive.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.fm.contact.api.jsonapi.dto.ObjContactDto
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactRepository
import org.springframework.stereotype.Controller

/**
 * Generic API repository for ObjContact using the GenericAggregateDtoAdapter.
 *
 * This replaces ObjContactApiRepositoryImpl and uses metadata-driven serialization
 * instead of manual DTO mapping.
 */
@Controller("objContactApiRepository")
open class ObjContactDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjContactRepository,
	adapter: ObjContactDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjContact, ObjContactDto>(
		resourceClass = ObjContactDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
