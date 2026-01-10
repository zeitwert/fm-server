package io.zeitwert.fm.contact.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiResource
import io.zeitwert.fm.obj.adapter.api.jsonapi.base.ObjDtoBase

/**
 * Generic JSON API resource for ObjContact.
 *
 * Uses dynamic attribute handling from GenericResourceBase. Relationships are declared explicitly
 * for crnk registration.
 */
@JsonApiResource(type = "contact", resourcePath = "contact/contacts")
class ObjContactDto : ObjDtoBase()
