package io.zeitwert.fm.account.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.contact.adapter.api.jsonapi.dto.ObjContactDto
import io.zeitwert.fm.dms.adapter.api.jsonapi.dto.ObjDocumentDto
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase
import io.zeitwert.fm.oe.adapter.api.jsonapi.dto.ObjTenantDto

/**
 * Generic JSON API resource for ObjAccount.
 *
 * Uses dynamic attribute handling from GenericResourceBase. Relationships are declared explicitly
 * for crnk registration.
 */
@JsonApiResource(type = "account", resourcePath = "account/accounts")
class ObjAccountDto : ObjDtoBase<ObjAccount>() {

	@JsonApiRelation(serialize = SerializeType.LAZY)
	val tenantInfo: ObjTenantDto? = null

	@JsonApiRelationId
	var tenantInfoId: String? = null
		get() = getRelation("tenantInfo") as String?
		set(value) {
			setRelation("tenantInfo", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var mainContact: ObjContactDto? = null

	@JsonApiRelationId
	var mainContactId: String? = null
		get() = getRelation("mainContact") as String?
		set(value) {
			setRelation("mainContact", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY, idField = "contactIds")
	@Suppress("UNCHECKED_CAST")
	val contacts: List<ObjContactDto>? = null

	@JsonApiRelationId
	@Suppress("UNCHECKED_CAST")
	var contactIds: List<String>? = null
		get() = getRelation("contacts") as List<String>?
		set(value) {
			setRelation("contacts", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var logo: ObjDocumentDto? = null

	@JsonApiRelationId
	var logoId: String? = null
		get() = getRelation("logo") as String?
		set(value) {
			setRelation("logo", value)
			field = value
		}

}
