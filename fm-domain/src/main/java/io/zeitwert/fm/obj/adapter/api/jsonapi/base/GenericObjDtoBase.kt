package io.zeitwert.fm.obj.adapter.api.jsonapi.base

import dddrive.app.obj.model.Obj
import io.crnk.core.resource.annotations.JsonApiMetaInformation
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericAggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto

abstract class GenericObjDtoBase<O : Obj> :
	GenericAggregateDtoBase<O>(),
	GenericAggregateDto<O> {

	@JsonApiMetaInformation
	override var meta: AggregateMetaDto? = null
		get() = this["meta"] as? AggregateMetaDto
		set(value) {
			this["meta"] = value
			field = value
		}

}
