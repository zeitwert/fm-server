package io.zeitwert.fm.account.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.DtoUtils
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objAccountDtoAdapter")
class ObjAccountDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjAccount, ObjAccountDto>(directory, { ObjAccountDto() }) {

	init {
		config.relationship("tenantInfo", "tenant", "tenant")
		config.relationship("mainContact", "contact", "mainContact")
		config.relationship("logo", "document", "logoImage")
		config.relationship("contacts", "contact") { entity, dto ->
			(entity as ObjAccount).contactList.map { DtoUtils.idToString(it)!! }
		}
	}

}
