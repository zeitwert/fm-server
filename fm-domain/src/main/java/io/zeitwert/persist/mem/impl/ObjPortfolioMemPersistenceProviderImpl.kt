package io.zeitwert.persist.mem.impl

import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.persist.ObjPortfolioPersistenceProvider
import io.zeitwert.persist.mem.base.AggregateMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjPortfolio.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objPortfolioPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjPortfolioMemPersistenceProviderImpl :
	AggregateMemPersistenceProviderBase<ObjPortfolio>(ObjPortfolio::class.java),
	ObjPortfolioPersistenceProvider
