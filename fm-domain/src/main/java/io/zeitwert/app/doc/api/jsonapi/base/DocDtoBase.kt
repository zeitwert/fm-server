package io.zeitwert.app.doc.api.jsonapi.base

import io.zeitwert.app.api.jsonapi.ResourceDto
import io.zeitwert.app.api.jsonapi.base.AggregateDtoBase

abstract class DocDtoBase :
	AggregateDtoBase(),
	ResourceDto
