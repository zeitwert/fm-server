package io.zeitwert.persist.mem.impl

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.portfolio.model.ObjPortfolio
import io.zeitwert.persist.ObjPortfolioPersistenceProvider
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjPortfolio.
 *
 * Active when zeitwert.persistence_type=mem
 */
@Component("objPortfolioPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence_type"], havingValue = "mem")
class ObjPortfolioMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjMemPersistenceProviderBase<ObjPortfolio>(ObjPortfolio::class.java),
	ObjPortfolioPersistenceProvider
