package io.zeitwert.fm.portfolio.service.api;

import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.fm.portfolio.model.ObjPortfolio;

import java.util.Map;

public interface ObjPortfolioCache extends AggregateCache<ObjPortfolio> {

	Map<String, Integer> getStatistics();

}
