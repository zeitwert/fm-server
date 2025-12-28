package io.zeitwert.fm.account.model

import io.zeitwert.fm.obj.model.FMObjRepository
import java.util.*

interface ObjAccountRepository : FMObjRepository<ObjAccount> {

	fun getByKey(key: String): Optional<ObjAccount>

}
