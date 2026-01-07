package io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto

import dddrive.ddd.core.model.RepositoryDirectory
import io.crnk.core.resource.meta.MetaInformation
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.JsonDto
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

// ============================================================================
// Helper Classes for DTO Wrapping
// ============================================================================

class MapDto(
	val map: Map<String, Any?>,
) : Map<String, Any?> by map,
	JsonDto {

	override fun containsKey(key: String): Boolean = map.containsKey(key)

	override operator fun get(key: String): Any? = map[key]

	override operator fun set(
		key: String,
		value: Any?,
	) = TODO()

}

class MutableMapDto(
	private val map: MutableMap<String, Any?> = mutableMapOf(),
) : MutableMap<String, Any?> by map,
	JsonDto {

	override fun containsKey(key: String): Boolean = map.containsKey(key)

	override operator fun set(
		key: String,
		value: Any?,
	) {
		map[key] = value
	}

	override operator fun get(key: String): Any? = map[key]

}

class MetaInfoDto(
	private val map: MutableMap<String, Any?> = mutableMapOf(),
) : MutableMap<String, Any?> by map,
	JsonDto,
	MetaInformation {

	override fun containsKey(key: String): Boolean = map.containsKey(key)

	override operator fun set(
		key: String,
		value: Any?,
	) {
		map[key] = value
	}

	override operator fun get(key: String): Any? = map[key]

}
