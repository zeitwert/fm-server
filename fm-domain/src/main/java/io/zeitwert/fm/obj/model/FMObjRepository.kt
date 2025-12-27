package io.zeitwert.fm.obj.model

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository
import io.zeitwert.dddrive.model.FMAggregateRepository

interface FMObjRepository<O : Obj> :
	ObjRepository<O>,
	FMAggregateRepository
