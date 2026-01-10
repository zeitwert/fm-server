package io.zeitwert.fm.obj.adapter.api.jsonapi.base

import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase

abstract class ObjDtoBase :
	AggregateDtoBase(),
	AggregateDto
