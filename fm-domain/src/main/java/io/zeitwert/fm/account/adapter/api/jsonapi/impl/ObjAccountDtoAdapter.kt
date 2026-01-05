package io.zeitwert.fm.account.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import dddrive.ddd.query.query
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.DtoUtils
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoAdapterBase
import org.springframework.stereotype.Component

@Component("objAccountDtoAdapter")
class ObjAccountDtoAdapter(
	directory: RepositoryDirectory,
) : ObjDtoAdapterBase<ObjAccount, ObjAccountDto>(directory, { ObjAccountDto() }) {

	init {
		config.exclude("mainContact")
		config.relationship("mainContactId", "contact") { entity, dto ->
			val accountId = (entity as ObjAccount).id
			val querySpec = query {
				filter { "accountId" eq accountId }
			}
			val contactIds = directory.getRepository(ObjContact::class.java).find(querySpec)
			contactIds.map { DtoUtils.idToString(it) }.firstOrNull()
		}
		config.relationship("logoId", "document", "logoImage")
		config.relationshipSet("contactIds", "contact") { entity, dto ->
			val accountId = (entity as ObjAccount).id
			val querySpec = query {
				filter { "accountId" eq accountId }
			}
			val contactIds = directory.getRepository(ObjContact::class.java).find(querySpec)
			contactIds.map { DtoUtils.idToString(it)!! }
		}
	}

}
