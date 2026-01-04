package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.app.model.SessionContext
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateApiRepositoryBase
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import org.springframework.stereotype.Controller

@Controller("objPortfolioApiRepository")
open class ObjPortfolioApiRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjPortfolioRepository,
	adapter: ObjPortfolioDtoAdapter,
	sessionCtx: SessionContext,
) : GenericAggregateApiRepositoryBase<ObjPortfolio, ObjPortfolioDto>(
		resourceClass = ObjPortfolioDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
