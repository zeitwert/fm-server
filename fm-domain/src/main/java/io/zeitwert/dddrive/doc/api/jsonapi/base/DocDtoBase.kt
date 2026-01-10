package io.zeitwert.dddrive.doc.api.jsonapi.base

import io.zeitwert.api.jsonapi.ResourceDto
import io.zeitwert.dddrive.api.jsonapi.base.AggregateDtoBase

abstract class DocDtoBase :
	AggregateDtoBase(),
	ResourceDto
