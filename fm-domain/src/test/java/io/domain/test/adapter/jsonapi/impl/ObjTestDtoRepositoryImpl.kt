package io.domain.test.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.domain.test.adapter.jsonapi.dto.ObjTestDto
import io.domain.test.model.ObjTest
import io.domain.test.model.ObjTestRepository
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import org.springframework.stereotype.Controller

/**
 * Generic API repository for ObjAccount using the GenericAggregateDtoAdapter.
 *
 * This replaces ObjAccountApiRepositoryImpl and uses metadata-driven serialization
 * instead of manual DTO mapping.
 */
@Controller("objTestApiRepository")
open class ObjTestDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjTestRepository,
	adapter: ObjTestDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjTest, ObjTestDto>(
	resourceClass = ObjTestDto::class.java,
	directory = directory,
	repository = repository,
	adapter = adapter,
	sessionCtx = sessionCtx,
)
