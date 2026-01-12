package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.oe.model.ObjTenant
import java.util.*

interface ObjTenantPersistenceProvider : AggregatePersistenceProvider<ObjTenant> {

	fun getByKey(key: String): Optional<Any>

}
