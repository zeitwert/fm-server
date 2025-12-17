package io.dddrive.core.oe.model

import io.dddrive.core.obj.model.Obj

interface ObjUser : Obj {

	var email: String?

	var password: String?

	var name: String?

	var description: String?

}
