package io.zeitwert.dddrive.doc.model.impl

import io.zeitwert.dddrive.doc.model.FMDocVRepository
import io.zeitwert.dddrive.doc.model.base.FMDocBase

class DocVImpl(
	override val repository: FMDocVRepository,
	isNew: Boolean,
) : FMDocBase(repository, isNew)
