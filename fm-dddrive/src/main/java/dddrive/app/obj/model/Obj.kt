package dddrive.app.obj.model

import dddrive.app.ddd.model.Aggregate

interface Obj : Aggregate {

	override val meta: ObjMeta

}
