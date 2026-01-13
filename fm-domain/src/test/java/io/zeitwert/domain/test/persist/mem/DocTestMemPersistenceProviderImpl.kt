package io.zeitwert.domain.test.persist.mem

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.domain.test.model.DocTest
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.persist.DocTestPersistenceProvider
import io.zeitwert.domain.test.persist.ObjTestPersistenceProvider
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.persist.ObjNotePersistenceProvider
import io.zeitwert.persist.mem.base.DocMemPersistenceProviderBase
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component("docTestPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class DocTestMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : DocMemPersistenceProviderBase<DocTest>(DocTest::class.java),
	DocTestPersistenceProvider
