package io.zeitwert.fm.doc.adapter.api.jsonapi.base

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase

abstract class DocDtoBase :
	AggregateDtoBase(),
	AggregateDto
