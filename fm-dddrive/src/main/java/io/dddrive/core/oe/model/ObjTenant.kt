package io.dddrive.core.oe.model

import io.dddrive.core.obj.model.Obj

interface ObjTenant : Obj {

	var key: String?

	var name: String?

	var description: String?

}
