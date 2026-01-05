package io.zeitwert.fm.account.model

import dddrive.app.obj.model.ObjRepository
import java.util.*

interface ObjAccountRepository : ObjRepository<ObjAccount> {

	fun getByKey(key: String): Optional<ObjAccount>

}
