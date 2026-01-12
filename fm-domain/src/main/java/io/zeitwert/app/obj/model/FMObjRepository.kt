package io.zeitwert.app.obj.model

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository

interface FMObjRepository : ObjRepository<Obj> {

	fun isObj(id: Any): Boolean

}
