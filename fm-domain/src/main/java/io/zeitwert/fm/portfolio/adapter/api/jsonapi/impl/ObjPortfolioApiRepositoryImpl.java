package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import io.zeitwert.dddrive.app.model.SessionContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.fm.oe.model.ObjUserRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.ObjPortfolioApiRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import org.springframework.stereotype.Controller;

@Controller("objPortfolioApiRepository")
public class ObjPortfolioApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjPortfolio, ObjPortfolioDto>
		implements ObjPortfolioApiRepository {

	public ObjPortfolioApiRepositoryImpl(
			ObjPortfolioRepository repository,
			SessionContext requestCtx,
			ObjUserRepository userRepository,
			ObjPortfolioDtoAdapter dtoAdapter) {
		super(ObjPortfolioDto.class, requestCtx, userRepository, repository, dtoAdapter);
	}

}
