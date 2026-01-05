package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import dddrive.app.doc.model.Doc
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase

abstract class DocDtoBase<D : Doc> :
	AggregateDtoBase<D>(),
	AggregateDto<D>
