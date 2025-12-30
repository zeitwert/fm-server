package io.zeitwert.fm.obj.model.impl

import io.zeitwert.fm.obj.model.FMObjVRepository
import io.zeitwert.fm.obj.model.base.FMObjBase

class ObjVImpl(
	override val repository: FMObjVRepository,
	isNew: Boolean,
) : FMObjBase(repository, isNew)
