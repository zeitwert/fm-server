package io.zeitwert.fm.doc.model.impl

import io.zeitwert.fm.doc.model.FMDocVRepository
import io.zeitwert.fm.doc.model.base.FMDocBase

class DocVImpl(
	override val repository: FMDocVRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew)
