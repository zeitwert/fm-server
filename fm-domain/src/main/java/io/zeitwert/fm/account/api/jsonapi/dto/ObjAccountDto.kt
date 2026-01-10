package io.zeitwert.fm.account.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.app.obj.api.jsonapi.base.ObjDtoBase

/**
 * JSON API resource for ObjAccount.
 *
 * Relationship fields are declared for Crnk registration but use the base class's relation storage
 * via getRelation/setRelation.
 */
@JsonApiResource(type = "account", resourcePath = "account/accounts")
class ObjAccountDto : ObjDtoBase()
