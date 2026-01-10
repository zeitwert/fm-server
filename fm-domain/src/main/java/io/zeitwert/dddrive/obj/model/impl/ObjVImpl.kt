package io.zeitwert.dddrive.obj.model.impl

import io.zeitwert.dddrive.obj.model.FMObjVRepository
import io.zeitwert.dddrive.obj.model.base.FMObjBase

class ObjVImpl(
	override val repository: FMObjVRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew)
