package io.zeitwert.fm.obj.adapter.api.jsonapi.base

import dddrive.app.obj.model.Obj
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.GenericAggregateDto
import io.zeitwert.dddrive.ddd.adapter.api.jsonapi.base.GenericAggregateDtoBase

abstract class GenericObjDtoBase<O : Obj> :
	GenericAggregateDtoBase<O>(),
	GenericAggregateDto<O>
