package io.dddrive.oe.model

import dddrive.app.obj.model.Obj

interface ObjTenant : Obj {

	var key: String?

	var name: String?

	var description: String?

}
