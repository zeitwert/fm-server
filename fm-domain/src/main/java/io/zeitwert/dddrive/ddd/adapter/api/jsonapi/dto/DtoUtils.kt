package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.fm.oe.model.ObjTenant
import io.zeitwert.fm.oe.model.ObjTenantRepository

object DtoUtils {

	var tenantRepo: ObjTenantRepository? = null

	fun idToString(id: Any?): String? {
		if (id == null) {
			return null
		} else if (tenantRepo == null) {
			tenantRepo = RepositoryDirectory.instance.getRepository(ObjTenant::class.java) as ObjTenantRepository
		}
		return tenantRepo!!.idToString(id)
	}

	fun idFromString(dtoId: String?): Any? {
		if (dtoId == null) {
			return null
		} else if (tenantRepo == null) {
			tenantRepo = RepositoryDirectory.instance.getRepository(ObjTenant::class.java) as ObjTenantRepository
		}
		return tenantRepo!!.idFromString(dtoId)
	}

}
