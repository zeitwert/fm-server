package io.zeitwert.fm.portfolio.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.base.AggregateDtoRepositoryBase
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.portfolio.api.jsonapi.dto.ObjPortfolioDto
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository
import org.springframework.stereotype.Controller

@Controller("objPortfolioApiRepository")
open class ObjPortfolioDtoRepositoryImpl(
	directory: RepositoryDirectory,
	repository: ObjPortfolioRepository,
	adapter: ObjPortfolioDtoAdapter,
	sessionCtx: SessionContext,
) : AggregateDtoRepositoryBase<ObjPortfolio, ObjPortfolioDto>(
		resourceClass = ObjPortfolioDto::class.java,
		directory = directory,
		repository = repository,
		adapter = adapter,
		sessionCtx = sessionCtx,
	)
