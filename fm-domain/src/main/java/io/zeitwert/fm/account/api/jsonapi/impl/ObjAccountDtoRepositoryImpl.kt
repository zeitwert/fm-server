package io.zeitwert.fm.account.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.account.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.account.model.ObjAccountRepository
import org.springframework.stereotype.Controller

/**
 * Generic API repository for ObjAccount using the GenericAggregateDtoAdapter.
 *
 * This replaces ObjAccountApiRepositoryImpl and uses metadata-driven serialization
 * instead of manual DTO mapping.
 */
@Controller("objAccountApiRepository")
open class ObjAccountDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjAccountRepository,
	adapter: ObjAccountDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjAccount, ObjAccountDto>(
		resourceClass = ObjAccountDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
