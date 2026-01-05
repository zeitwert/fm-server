package io.zeitwert.fm.oe.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjUserDto
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.springframework.stereotype.Component

@Component("objUserDtoAdapter")
class ObjUserDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjUser, ObjUserDto>(directory, { ObjUserDto() }) {

	init {
		config.relationship("avatarId", "document", "avatarImage")
		config.exclude("tenantSet")
		config.field("tenants", "tenantSet")
	}

	fun asEnumerated(obj: ObjUser?): EnumeratedDto? = if (obj == null) null else EnumeratedDto.of("" + obj.id, obj.caption)

	override fun toAggregate(
		dto: ObjUserDto,
		aggregate: ObjUser,
	) {
		super.toAggregate(dto, aggregate)
		// Handle password and role updates
		val dtoId = dto["id"] as String?
		val password = dto["password"] as String?
		if (dtoId != null && password != null) {
			aggregate.password = password
			aggregate.needPasswordChange = dto["needPasswordChange"] as Boolean?
		} else {
			aggregate.email = dto["email"] as String?
			if (dtoId == null) {
				aggregate.password = password
				aggregate.needPasswordChange = dto["needPasswordChange"] as Boolean?
			}
			aggregate.name = dto["name"] as String?
			aggregate.description = dto["description"] as String?
			@Suppress("UNCHECKED_CAST")
			val roleMap = dto["role"] as? Map<String, Any?>
			if (roleMap != null) {
				aggregate.role = CodeUserRole.getUserRole(roleMap["id"] as String?)
			}
		}
	}

}
