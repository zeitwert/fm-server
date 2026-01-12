package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.portfolio.model.ObjPortfolio

interface ObjPortfolioPersistenceProvider : AggregatePersistenceProvider<ObjPortfolio> {

}
