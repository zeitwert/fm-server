package io.zeitwert.fm.oe.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.dto.EnumeratedDto
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.oe.adapter.jsonapi.dto.ObjTenantDto
import io.zeitwert.fm.oe.model.ObjTenant
import org.springframework.stereotype.Component

@Component("objTenantDtoAdapter")
class ObjTenantDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjTenant, ObjTenantDto>(
		ObjTenant::class.java,
		"tenant",
		ObjTenantDto::class.java,
		directory,
		{ ObjTenantDto() },
	) {

	init {
		config.relationship("logo", "document", "logoImage")
	}

	fun asEnumerated(obj: ObjTenant?): EnumeratedDto? = if (obj == null) null else EnumeratedDto.of("" + obj.id, obj.caption)

}
