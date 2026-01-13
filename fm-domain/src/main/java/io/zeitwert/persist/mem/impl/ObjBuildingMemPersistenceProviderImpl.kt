package io.zeitwert.persist.mem.impl

import io.zeitwert.app.session.model.KernelContext
import io.zeitwert.app.session.model.SessionContext
import io.zeitwert.fm.building.model.ObjBuilding
import io.zeitwert.persist.ObjBuildingPersistenceProvider
import io.zeitwert.persist.mem.base.ObjMemPersistenceProviderBase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Memory-based persistence provider for ObjBuilding.
 *
 * Active when zeitwert.persistence.type=mem
 */
@Component("objBuildingPersistenceProvider")
@ConditionalOnProperty(name = ["zeitwert.persistence.type"], havingValue = "mem")
class ObjBuildingMemPersistenceProviderImpl(
	override val sessionContext: SessionContext,
	override val kernelContext: KernelContext,
) : ObjMemPersistenceProviderBase<ObjBuilding>(ObjBuilding::class.java),
	ObjBuildingPersistenceProvider
