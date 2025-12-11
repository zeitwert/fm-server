
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import org.springframework.stereotype.Controller;

import io.zeitwert.dddrive.app.model.RequestContext;
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateApiRepositoryBase;
import io.dddrive.oe.service.api.ObjUserCache;
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

	public ObjPortfolioApiRepositoryImpl(
			ObjPortfolioRepository repository,
			RequestContext requestCtx,
			ObjUserCache userCache,
			ObjPortfolioDtoAdapter dtoAdapter) {
		super(ObjPortfolioDto.class, requestCtx, userCache, repository, dtoAdapter);
	}

}
