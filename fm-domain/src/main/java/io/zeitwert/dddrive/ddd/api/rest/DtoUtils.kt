package io.zeitwert.dddrive.ddd.api.rest

import dddrive.ddd.core.model.RepositoryDirectory
import io.dddrive.oe.model.ObjTenant

object DtoUtils {

	@JvmStatic
	fun idToString(id: Any?): String = RepositoryDirectory.instance.getRepository(ObjTenant::class.java).idToString(id)!!

	@JvmStatic
	fun idFromString(id: String): Any? = RepositoryDirectory.instance.getRepository(ObjTenant::class.java).idFromString(id)

}
