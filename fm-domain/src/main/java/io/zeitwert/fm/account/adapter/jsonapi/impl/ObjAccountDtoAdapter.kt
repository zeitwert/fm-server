package io.zeitwert.fm.account.adapter.jsonapi.impl

import dddrive.ddd.model.RepositoryDirectory
import io.zeitwert.app.api.jsonapi.dto.DtoUtils
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoAdapterBase
import io.zeitwert.fm.account.adapter.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.account.model.ObjAccount
import org.springframework.stereotype.Component

@Component("objAccountDtoAdapter")
class ObjAccountDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjAccount, ObjAccountDto>(
		ObjAccount::class.java,
		"account",
		ObjAccountDto::class.java,
		directory,
		{ ObjAccountDto() },
	) {

	init {
		config.relationship("mainContact", "contact", "mainContact")
		config.relationship("logo", "document", "logoImage")
		config.relationshipMany("contacts", "contact") { entity, dto ->
			(entity as ObjAccount).contactList.map { DtoUtils.idToString(it)!! }
		}
	}

}
