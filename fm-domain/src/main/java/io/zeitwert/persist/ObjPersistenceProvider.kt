package io.zeitwert.persist

import dddrive.app.obj.model.Obj
import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec

interface ObjPersistenceProvider : AggregatePersistenceProvider<Obj> {

	fun isObj(id: Any): Boolean

}
