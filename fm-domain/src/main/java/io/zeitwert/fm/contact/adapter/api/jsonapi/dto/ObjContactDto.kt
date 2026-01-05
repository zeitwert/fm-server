package io.zeitwert.fm.contact.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase

/**
 * Generic JSON API resource for ObjContact.
 *
 * Uses dynamic attribute handling from GenericResourceBase. Relationships are declared explicitly
 * for crnk registration.
 */
@JsonApiResource(type = "contact", resourcePath = "contact/contacts")
class ObjContactDto : ObjDtoBase<ObjContact>() {

	@JsonApiRelationId
	var accountId: String? = null
		get() = getRelation("accountId") as String?
		set(value) {
			setRelation("accountId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var account: ObjAccountDto? = null

}
