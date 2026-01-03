package io.zeitwert.fm.contact.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto
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
open class ObjContactApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjContactRepository,
	adapter: ObjContactDtoAdapter,
	sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjContact, ObjContactDto>(
		resourceClass = ObjContactDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
