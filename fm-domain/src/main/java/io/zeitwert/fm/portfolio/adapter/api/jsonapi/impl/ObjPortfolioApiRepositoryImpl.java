
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.model.ObjUserFMRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.ObjPortfolioApiRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;

@Controller("objPortfolioApiRepository")
public class ObjPortfolioApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjPortfolio, ObjPortfolioDto>
		implements ObjPortfolioApiRepository {

	public ObjPortfolioApiRepositoryImpl(
			ObjPortfolioRepository repository,
			RequestContext requestCtx,
			ObjUserFMRepository userCache,
			ObjPortfolioDtoAdapter dtoAdapter) {
		super(ObjPortfolioDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
