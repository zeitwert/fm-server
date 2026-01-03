package io.zeitwert.fm.account.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.DtoUtils
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.GenericObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objAccountDtoAdapter")
class ObjAccountDtoAdapter(
	directory: RepositoryDirectory,
) : GenericObjDtoAdapterBase<ObjAccount, ObjAccountDto>(directory, { ObjAccountDto() }) {

	init {
		exclude("mainContact")
		relationship("mainContactId", "contact") { entity, dto ->
			val accountId = (entity as ObjAccount).id
			val contactIds = directory.getRepository(ObjContact::class.java).getByForeignKey("accountId", accountId)
			contactIds.map { DtoUtils.idToString(it) }.random()
		}
		relationship("logoId", "document", "logoImage")
		relationshipSet("contactIds", "contact") { entity, dto ->
			val accountId = (entity as ObjAccount).id
			val contactIds = directory.getRepository(ObjContact::class.java).getByForeignKey("accountId", accountId)
			contactIds.map { DtoUtils.idToString(it) }
		}
	}

}
