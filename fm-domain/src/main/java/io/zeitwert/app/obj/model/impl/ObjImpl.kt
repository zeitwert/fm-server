package io.zeitwert.app.obj.model.impl

import io.zeitwert.app.obj.model.FMObjRepository
import io.zeitwert.app.obj.model.base.FMObjBase

class ObjImpl(
	override val repository: FMObjRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew)
