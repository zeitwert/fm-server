
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.zeitwert.ddd.oe.service.api.UserService;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.ObjPortfolioApiRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDtoAdapter;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;

@Controller("objPortfolioApiRepository")
public class ObjPortfolioApiRepositoryImpl
		extends AggregateApiRepositoryBase<ObjPortfolio, ObjPortfolioVRecord, ObjPortfolioDto>
		implements ObjPortfolioApiRepository {

	public ObjPortfolioApiRepositoryImpl(ObjPortfolioRepository repository, RequestContext requestCtx,
			UserService userService) {
		super(ObjPortfolioDto.class, requestCtx, userService, repository, ObjPortfolioDtoAdapter.getInstance());
	}

}
