package io.zeitwert.fm.account.adapter.api.jsonapi.impl

import dddrive.ddd.core.model.RepositoryDirectory
import io.crnk.core.queryspec.FilterOperator
import io.crnk.core.queryspec.PathSpec
import io.crnk.core.queryspec.QuerySpec
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
		exclude("mainContact")
		relationship("mainContactId", "contact") { entity, dto ->
			val accountId = (entity as ObjAccount).id
			val query = QuerySpec(ObjContact::class.java).apply {
				addFilter(PathSpec.of("accountId").filter(FilterOperator.EQ, accountId))
			}
			val contactIds = directory.getRepository(ObjContact::class.java).find(query)
			contactIds.map { DtoUtils.idToString(it) }.firstOrNull()
		}
		relationship("logoId", "document", "logoImage")
		relationshipSet("contactIds", "contact") { entity, dto ->
			val accountId = (entity as ObjAccount).id
			val query = QuerySpec(ObjContact::class.java).apply {
				addFilter(PathSpec.of("accountId").filter(FilterOperator.EQ, accountId))
			}
			val contactIds = directory.getRepository(ObjContact::class.java).find(query)
			contactIds.map { DtoUtils.idToString(it)!! }
		}
	}

}
