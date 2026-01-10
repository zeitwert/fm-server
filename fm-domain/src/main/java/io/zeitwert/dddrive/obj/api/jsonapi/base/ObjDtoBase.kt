package io.zeitwert.dddrive.obj.api.jsonapi.base

import io.zeitwert.api.jsonapi.ResourceDto
import io.zeitwert.dddrive.api.jsonapi.base.AggregateDtoBase

abstract class ObjDtoBase :
	AggregateDtoBase(),
	ResourceDto
