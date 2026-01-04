package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericAggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoBase

abstract class GenericDocDtoBase<D : Doc> :
	GenericAggregateDtoBase<D>(),
	GenericAggregateDto<D>
