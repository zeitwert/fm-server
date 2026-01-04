package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import io.crnk.core.resource.annotations.JsonApiMetaInformation
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericAggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoBase
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.dto.AggregateMetaDto

abstract class GenericDocDtoBase<D : Doc> :
	GenericAggregateDtoBase<D>(),
	GenericAggregateDto<D> {

	@JsonApiMetaInformation
	override var meta: AggregateMetaDto? = null
		get() = this["meta"] as? AggregateMetaDto
		set(value) {
			this["meta"] = value
			field = value
		}

}
