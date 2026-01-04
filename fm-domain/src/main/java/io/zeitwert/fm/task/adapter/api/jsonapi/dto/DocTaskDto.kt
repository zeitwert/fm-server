package io.zeitwert.fm.task.adapter.api.jsonapi.dto

import io.crnk.core.resource.annotations.JsonApiField
import io.crnk.core.resource.annotations.JsonApiRelation
import io.crnk.core.resource.annotations.JsonApiRelationId
import io.crnk.core.resource.annotations.JsonApiResource
import io.crnk.core.resource.annotations.SerializeType
import io.zeitwert.fm.account.adapter.api.jsonapi.dto.ObjAccountDto
import io.zeitwert.fm.doc.adapter.api.jsonapi.base.GenericDocDtoBase
import io.zeitwert.fm.task.model.DocTask

@JsonApiResource(type = "task", resourcePath = "collaboration/tasks")
class DocTaskDto : GenericDocDtoBase<DocTask>() {

	@JsonApiRelationId
	var accountId: String? = null
		get() = getRelation("accountId") as String?
		set(value) {
			setRelation("accountId", value)
			field = value
		}

	@JsonApiRelation(serialize = SerializeType.LAZY)
	var account: ObjAccountDto? = null

	@JsonApiField(readable = false, filterable = true)
	fun getRelatedToId(): Int? = null

}
