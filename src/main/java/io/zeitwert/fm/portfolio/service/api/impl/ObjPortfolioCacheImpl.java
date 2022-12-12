package io.zeitwert.fm.portfolio.service.api.impl;

import org.springframework.stereotype.Service;

import io.zeitwert.ddd.aggregate.service.api.base.AggregateCacheBase;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;
import io.zeitwert.fm.portfolio.model.ObjPortfolioRepository;
import io.zeitwert.fm.portfolio.service.api.ObjPortfolioCache;

@Service("portfolioCache")
public class ObjPortfolioCacheImpl extends AggregateCacheBase<ObjPortfolio> implements ObjPortfolioCache {

	public ObjPortfolioCacheImpl(ObjPortfolioRepository repository) {
		super(repository, ObjPortfolio.class);
	}

}
