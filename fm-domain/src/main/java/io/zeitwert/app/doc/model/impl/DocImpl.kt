package io.zeitwert.app.doc.model.impl

import io.zeitwert.app.doc.model.FMDocRepository
import io.zeitwert.app.doc.model.base.FMDocBase

class DocImpl(
	override val repository: FMDocRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew)
