package io.dddrive.oe.model

import io.dddrive.obj.model.Obj

interface ObjTenant : Obj {

	var key: String?

	var name: String?

	var description: String?

}
