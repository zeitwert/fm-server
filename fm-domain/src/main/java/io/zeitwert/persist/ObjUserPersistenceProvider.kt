package io.zeitwert.persist

import dddrive.ddd.model.AggregatePersistenceProvider
import dddrive.query.QuerySpec
import io.zeitwert.fm.oe.model.ObjUser
import java.util.*

interface ObjUserPersistenceProvider : AggregatePersistenceProvider<ObjUser> {

	fun getByEmail(email: String): Optional<Any>

}
