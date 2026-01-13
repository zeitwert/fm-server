package io.zeitwert.domain.test.persist.mem

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.domain.test.model.ObjTest
import io.zeitwert.domain.test.persist.ObjTestPersistenceProvider
import io.zeitwert.fm.collaboration.model.ObjNote
import io.zeitwert.persist.ObjNotePersistenceProvider
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component("objTestPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjTestMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjMemPersistenceProviderBase<ObjTest>(ObjTest::class.java),
	ObjTestPersistenceProvider
