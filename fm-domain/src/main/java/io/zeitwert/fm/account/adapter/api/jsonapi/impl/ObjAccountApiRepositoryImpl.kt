package io.zeitwert.fm.account.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import io.zeitwert.fm.app.model.SessionContextFM
import org.springframework.stereotype.Controller

/**
 * Generic API repository for ObjAccount using the GenericAggregateDtoAdapter.
 *
 * This replaces ObjAccountApiRepositoryImpl and uses metadata-driven serialization
 * instead of manual DTO mapping.
 */
@Controller("objAccountApiRepository")
open class ObjAccountApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjAccountRepository,
	adapter: ObjAccountDtoAdapter,
	sessionCtx: SessionContextFM,
) : GenericAggregateApiRepositoryBase<ObjAccount, ObjAccountDto>(
		resourceClass = ObjAccountDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
