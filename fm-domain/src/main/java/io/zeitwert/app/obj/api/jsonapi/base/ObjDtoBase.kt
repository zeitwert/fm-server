package io.zeitwert.app.obj.api.jsonapi.base

import io.zeitwert.app.api.jsonapi.ResourceDto
import io.zeitwert.app.api.jsonapi.base.AggregateDtoBase

abstract class ObjDtoBase :
	AggregateDtoBase(),
	ResourceDto
