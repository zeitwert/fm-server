
package io.zeitwert.fm.portfolio.adapter.api.jsonapi.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.zeitwert.fm.portfolio.adapter.api.jsonapi.ObjPortfolioApiRepository;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDto;
import io.zeitwert.fm.portfolio.adapter.api.jsonapi.dto.ObjPortfolioDtoBridge;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.model.db.tables.records.ObjPortfolioVRecord;
import io.zeitwert.ddd.aggregate.adapter.api.jsonapi.base.AggregateApiAdapter;
import io.zeitwert.ddd.session.model.SessionInfo;

@Controller("objPortfolioApiRepository")
public class ObjPortfolioApiRepositoryImpl
		extends AggregateApiAdapter<ObjPortfolio, ObjPortfolioVRecord, ObjPortfolioDto>
		implements ObjPortfolioApiRepository {

	@Autowired
	public ObjPortfolioApiRepositoryImpl(final ObjPortfolioRepository repository, SessionInfo sessionInfo) {
		super(ObjPortfolioDto.class, sessionInfo, repository, ObjPortfolioDtoBridge.getInstance());
	}

}
