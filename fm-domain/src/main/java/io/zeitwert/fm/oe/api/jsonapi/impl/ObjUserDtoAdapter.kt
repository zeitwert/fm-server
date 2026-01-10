package io.zeitwert.fm.oe.api.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.oe.api.jsonapi.dto.ObjUserDto
import io.zeitwert.fm.oe.model.ObjUser
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.springframework.stereotype.Component

@Component("objUserDtoAdapter")
class ObjUserDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjUser, ObjUserDto>(
		ObjUser::class.java,
		"user",
		ObjUserDto::class.java,
		directory,
		{ ObjUserDto() },
	) {

	init {
		config.relationship("avatar", "document", "avatarImage")
		config.field("tenants", "tenantSet")
	}

	fun asEnumerated(obj: ObjUser?): EnumeratedDto? = if (obj == null) null else EnumeratedDto.of("" + obj.id, obj.caption)

	override fun toAggregate(
		dto: ObjUserDto,
		aggregate: ObjUser,
	) {
		super.toAggregate(dto, aggregate)
		// Handle password and role updates
		val dtoId = dto.getAttribute("id") as String?
		val password = dto.getAttribute("password") as String?
		if (dtoId != null && password != null) {
			aggregate.password = password
			aggregate.needPasswordChange = dto.getAttribute("needPasswordChange") as Boolean?
		} else {
			aggregate.email = dto.getAttribute("email") as String?
			if (dtoId == null) {
				aggregate.password = password
				aggregate.needPasswordChange = dto.getAttribute("needPasswordChange") as Boolean?
			}
			aggregate.name = dto.getAttribute("name") as String?
			aggregate.description = dto.getAttribute("description") as String?
			@Suppress("UNCHECKED_CAST")
			val roleMap = dto.getAttribute("role") as? Map<String, Any?>
			if (roleMap != null) {
				aggregate.role = CodeUserRole.getUserRole(roleMap["id"] as String?)
			}
		}
	}

}
