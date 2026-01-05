package io.zeitwert.fm.oe.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto
import io.zeitwert.fm.oe.model.ObjTenant
import org.springframework.stereotype.Component

@Component("objTenantDtoAdapter")
class ObjTenantDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjTenant, ObjTenantDto>(directory, { ObjTenantDto() }) {

	init {
		relationship("logoId", "document", "logoImage")
	}

	fun asEnumerated(obj: ObjTenant?): EnumeratedDto? = if (obj == null) null else EnumeratedDto.of("" + obj.id, obj.caption)

}
