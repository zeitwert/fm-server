package io.zeitwert.fm.obj.model

import dddrive.app.obj.model.Obj
import dddrive.app.obj.model.ObjRepository

interface FMObjVRepository : ObjRepository<Obj> {

	fun isObj(id: Any): Boolean

}
