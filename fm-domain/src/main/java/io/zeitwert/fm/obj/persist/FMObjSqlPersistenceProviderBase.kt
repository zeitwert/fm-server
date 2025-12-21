package io.zeitwert.fm.obj.persist

import io.dddrive.core.obj.model.Obj
import io.dddrive.core.obj.model.ObjPartTransition
import io.dddrive.core.property.model.PartListProperty
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase

abstract class FMObjSqlPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : AggregateSqlPersistenceProviderBase<O>(intfClass) {

	override fun doLoadParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginLoad()
			loadParts(
				aggregate.getProperty(
					"transitionList",
					ObjPartTransition::class,
				) as PartListProperty<ObjPartTransition>,
				"obj.transitionList",
			)
			endLoad()
		}
	}

	override fun doStoreParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).apply {
			beginStore()
			addParts(
				aggregate.getProperty("transitionList", ObjPartTransition::class) as PartListProperty<ObjPartTransition>,
				"obj.transitionList",
			)
			endStore()
		}
	}

}
