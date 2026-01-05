package io.zeitwert.fm.obj.adapter.api.jsonapi.base

import dddrive.app.obj.model.Obj
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.AggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.AggregateDtoBase

abstract class ObjDtoBase<O : Obj> :
	AggregateDtoBase<O>(),
	AggregateDto<O>
