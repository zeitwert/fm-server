package io.zeitwert.fm.obj.persist

import io.dddrive.obj.model.Obj
import io.zeitwert.dddrive.persist.base.AggregateSqlPersistenceProviderBase
import io.zeitwert.dddrive.persist.util.SqlUtils

abstract class FMObjSqlPersistenceProviderBase<O : Obj>(
	intfClass: Class<O>,
) : AggregateSqlPersistenceProviderBase<O>(intfClass) {

	override val hasAccount = true

	override val sqlUtils = SqlUtils()

	override fun doLoadParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doLoadParts {
			loadPartList(aggregate, "transitionList", "obj.transitionList")
		}
	}

	override fun doStoreParts(aggregate: O) {
		ObjPartTransitionSqlPersistenceProviderImpl(dslContext, aggregate).doStoreParts {
			storePartList(aggregate, "transitionList", "obj.transitionList")
		}
	}

}
