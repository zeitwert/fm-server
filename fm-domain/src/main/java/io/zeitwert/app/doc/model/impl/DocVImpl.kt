package io.zeitwert.app.doc.model.impl

import io.zeitwert.app.doc.model.FMDocVRepository
import io.zeitwert.app.doc.model.base.FMDocBase

class DocVImpl(
	override val repository: FMDocVRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew)
