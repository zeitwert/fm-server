package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.account.model.ObjAccount
import java.util.*

interface ObjAccountPersistenceProvider : AggregatePersistenceProvider<ObjAccount> {

	fun getByKey(key: String): Optional<Any>

}
