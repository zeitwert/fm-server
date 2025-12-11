package io.zeitwert.dddrive.ddd.api.rest

import io.dddrive.core.ddd.model.RepositoryDirectory
import io.dddrive.core.oe.model.ObjTenant

object DtoUtils {
	@JvmStatic
	fun idToString(id: Any?): String = RepositoryDirectory.getInstance().getRepository(ObjTenant::class.java).idToString(id)

	@JvmStatic
	fun idFromString(id: String): Any? = RepositoryDirectory.getInstance().getRepository(ObjTenant::class.java).idFromString(id)
}
