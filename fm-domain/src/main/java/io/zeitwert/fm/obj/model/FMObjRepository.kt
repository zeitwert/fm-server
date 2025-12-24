package io.zeitwert.fm.obj.model

import io.dddrive.obj.model.Obj
import io.dddrive.obj.model.ObjRepository
import io.zeitwert.dddrive.model.FMAggregateRepository

interface FMObjRepository<O : Obj> :
	ObjRepository<O>,
	FMAggregateRepository
