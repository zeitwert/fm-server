package io.zeitwert.app.obj.model.impl

import io.zeitwert.app.obj.model.FMObjVRepository
import io.zeitwert.app.obj.model.base.FMObjBase

class ObjVImpl(
	override val repository: FMObjVRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew)
