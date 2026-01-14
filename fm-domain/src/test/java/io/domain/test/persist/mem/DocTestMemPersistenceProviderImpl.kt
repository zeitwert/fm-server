package io.domain.test.persist.mem

import io.domain.test.model.DocTest
import io.domain.test.persist.DocTestPersistenceProvider
import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.persist.mem.base.DocMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component("docTestPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class DocTestMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : DocMemPersistenceProviderBase<DocTest>(DocTest::class.java),
	DocTestPersistenceProvider
